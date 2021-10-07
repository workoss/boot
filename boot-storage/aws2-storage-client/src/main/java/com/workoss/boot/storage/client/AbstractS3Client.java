/*
 * Copyright 2019-2021 workoss (https://www.workoss.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.workoss.boot.storage.client;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.workoss.boot.storage.config.StorageClientConfig;
import com.workoss.boot.storage.exception.StorageDownloadException;
import com.workoss.boot.storage.exception.StorageException;
import com.workoss.boot.storage.interceptor.CustomEndpointExecutionInterceptor;
import com.workoss.boot.storage.model.*;
import com.workoss.boot.storage.util.HttpUtil;
import com.workoss.boot.storage.util.StorageUtil;
import com.workoss.boot.util.Lazy;
import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.collection.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.internal.util.Mimetype;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.utils.IoUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 模板方法 抽象共同部分
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
public abstract class AbstractS3Client implements StorageClient {

	private static final Logger log = LoggerFactory.getLogger(AbstractS3Client.class);

	protected StorageClientConfig config;

	private static final Lazy<CustomEndpointExecutionInterceptor> ENDPOINT_INTERCEPTOR_LAZY = Lazy
			.of(() -> new CustomEndpointExecutionInterceptor());

	private static S3AsyncClientBuilder S3_ASYNC_CLIENT_BUILDER;

	protected static Cache<String, StorageStsToken> STS_TOKEN_CACHE = Caffeine.newBuilder()
			.expireAfterWrite(12, TimeUnit.MINUTES).maximumSize(200)
			.removalListener((String key, StorageStsToken value, RemovalCause cause) -> {
				log.debug("【STORAGE】STS_TOKEN_CACHE KEY：{} cause:{}", key, cause);
			}).build();

	@Override
	public void init(StorageClientConfig config) {
		this.config = config;
		// 各客户端初始化
		this.initConfig(config);

		if (!useStsToken()) {
			this.S3_ASYNC_CLIENT_BUILDER = initS3AsyncClientBuilder();
		}
	}

	/**
	 * 客户端初始化配置
	 * @param config 配置信息
	 */
	protected abstract void initConfig(StorageClientConfig config);

	/**
	 * 创建s3客户端
	 * @param config config
	 * @param stsToken stsToken
	 * @return s3客户端
	 */
	protected abstract S3AsyncClient createClient(StorageClientConfig config, StorageStsToken stsToken);

	protected S3AsyncClient createS3Client(StorageClientConfig config, String endpoint, StorageStsToken stsToken) {

		AwsCredentials credentials = null;
		if (stsToken == null) {
			credentials = AwsBasicCredentials.create(config.getAccessKey(), config.getSecretKey());
		}
		else {
			credentials = AwsSessionCredentials.create(stsToken.getAccessKey(), stsToken.getSecretKey(),
					stsToken.getStsToken());
		}
		S3AsyncClientBuilder s3AsyncClientBuilder = S3_ASYNC_CLIENT_BUILDER == null ? initS3AsyncClientBuilder()
				: S3_ASYNC_CLIENT_BUILDER;
		return s3AsyncClientBuilder.credentialsProvider(StaticCredentialsProvider.create(credentials))
				.endpointOverride(URI.create(endpoint)).build();
	}

	protected S3AsyncClientBuilder initS3AsyncClientBuilder() {
		return S3AsyncClient.builder().overrideConfiguration(builder -> {
			builder.addExecutionInterceptor(ENDPOINT_INTERCEPTOR_LAZY.get());
		}).serviceConfiguration(builder -> {
			builder.pathStyleAccessEnabled(false);
		}).region(Region.of(type().name()));
	}

	/**
	 * 生成stsToken
	 * @param config 配置
	 * @param key 文件key
	 * @param action 操作
	 * @return stsToken
	 */
	protected abstract StorageStsToken getStsToken(StorageClientConfig config, String key, String action);

	/**
	 * 生成h5签名
	 * @param config 配置
	 * @param key 文件key
	 * @param mimeType 文件类型
	 * @param successActionStatus 200 可以nul
	 * @return 签名
	 */
	protected abstract StorageSignature generateSignagure(StorageClientConfig config, String key, String mimeType,
			String successActionStatus);

	protected S3AsyncClient getClient(String key, String action) {
		if (!useStsToken()) {
			return this.createClient(config, null);
		}
		long now = System.currentTimeMillis();
		String cacheKey = StorageUtil.generateCacheKey(action, key);
		StorageStsToken storageStsToken = STS_TOKEN_CACHE.getIfPresent(cacheKey);
		boolean cacheusable = storageStsToken != null && storageStsToken.getExpiration().getTime() > now;
		if (!cacheusable) {
			storageStsToken = getStsToken(config, key, action);
			Date expiration = storageStsToken.getExpiration();
			if (expiration != null && expiration.getTime() - now > 3 * 60 * 1000) {
				STS_TOKEN_CACHE.put(cacheKey, storageStsToken);
			}
		}
		if (storageStsToken == null) {
			throw new StorageException("00002", "securityToken获取失败");
		}

		config.setEndpoint(storageStsToken.getEndpoint().replaceAll(config.getBucketName() + StorageUtil.DOT, ""));
		return this.createClient(config, storageStsToken);
	}

	protected boolean useStsToken() {
		return StringUtils.isEmpty(config.getAccessKey()) && StringUtils.isEmpty(config.getSecretKey())
				&& StringUtils.isNotBlank(config.getTokenUrl());
	}

	@Override
	public List<StorageBucketInfo> listBuckets() {
		try (S3AsyncClient s3AsyncClient = getClient("", "listBuckets")) {
			ListBucketsResponse listBucketsResponse = s3AsyncClient.listBuckets().get();
			if (!listBucketsResponse.hasBuckets()) {
				return Collections.EMPTY_LIST;
			}
			return listBucketsResponse.buckets().stream()
					.map(bucket -> new StorageBucketInfo(bucket.name(),
							listBucketsResponse.owner() == null ? null : listBucketsResponse.owner().id(),
							bucket.creationDate() == null ? null : Date.from(bucket.creationDate())))
					.collect(Collectors.toList());

		}
		catch (S3Exception s3Exception) {
			throw throwS3Exception(s3Exception);
		}
		catch (Exception e) {
			throw new StorageException("0002", e);
		}
	}

	@Override
	public StorageBucketInfo getBucket() {
		try (S3AsyncClient s3AsyncClient = getClient("", "getBucket")) {
			ListBucketsResponse listBucketsResponse = s3AsyncClient.listBuckets().get();
			if (!listBucketsResponse.hasBuckets()) {
				return null;
			}
			return listBucketsResponse.buckets().stream().filter(bucket -> config.getBucketName().equals(bucket.name()))
					.map(bucket -> new StorageBucketInfo(bucket.name(),
							listBucketsResponse.owner() == null ? null : listBucketsResponse.owner().id(),
							bucket.creationDate() == null ? null : Date.from(bucket.creationDate())))
					.findFirst().orElse(null);
		}
		catch (S3Exception s3Exception) {
			AwsErrorDetails awsErrorDetails = s3Exception.awsErrorDetails();
			if (awsErrorDetails != null) {
				throw new StorageException(awsErrorDetails.errorCode(), awsErrorDetails.errorMessage());
			}
			throw new StorageException(s3Exception.requestId(), s3Exception);
		}
		catch (Exception e) {
			throw new StorageException("0002", e);
		}
	}

	@Override
	public boolean doesBucketExist() {
		try (S3AsyncClient s3AsyncClient = getClient("", "doesBucketExist")) {
			HeadBucketRequest headBucketRequest = HeadBucketRequest.builder().bucket(config.getBucketName()).build();
			HeadBucketResponse headBucketResponse = s3AsyncClient.headBucket(headBucketRequest).get();
			return true;
		}
		catch (S3Exception s3Exception) {
			throw throwS3Exception(s3Exception);
		}
		catch (Exception e) {
			throw new StorageException("0002", e);
		}
	}

	@Override
	public boolean doesObjectExist(String key) {
		key = formatKey(key, false);
		try (S3AsyncClient s3AsyncClient = getClient(key, "doesObjectExist")) {
			HeadObjectRequest headObjectRequest = HeadObjectRequest.builder().bucket(config.getBucketName()).key(key)
					.build();
			HeadObjectResponse headObjectResponse = s3AsyncClient.headObject(headObjectRequest).get();

		}
		catch (S3Exception s3Exception) {
			throw throwS3Exception(s3Exception);
		}
		catch (Exception e) {
			throw new StorageException("0002", e);
		}
		return false;
	}

	@Override
	public StorageFileInfo getObject(String key) {
		key = formatKey(key, false);
		try (S3AsyncClient s3AsyncClient = getClient(key, "getObject")) {
			GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(config.getBucketName()).key(key)
					.build();

			ResponseBytes<GetObjectResponse> responseBytes = s3AsyncClient
					.getObject(getObjectRequest, AsyncResponseTransformer.toBytes()).get();
			GetObjectResponse response = responseBytes.response();
			StorageFileInfo storageFileInfo = new StorageFileInfo().setBucketName(config.getBucketName()).setKey(key)
					.setHost(formatHost());
			Map<String, String> metaData = response.metadata();
			if (response.hasMetadata()) {
				storageFileInfo.setMetaData(new HashMap<>(response.metadata()));
			}
			storageFileInfo.setETag(response.eTag())
					.setLastModified(response.lastModified() == null ? null : response.lastModified().toEpochMilli())
					.setSize(response.contentLength());
			return storageFileInfo;

		}
		catch (S3Exception s3Exception) {
			throw throwS3Exception(s3Exception);
		}
		catch (Exception e) {
			throw new StorageException("0002", e);
		}
	}

	@Override
	public StorageFileInfoListing listObjects(String key, String delimiter, String nextToken, Integer maxKeys) {
		key = formatKey(key, true);
		try (S3AsyncClient s3AsyncClient = getClient(key, "listObjects")) {
			ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder().bucket(config.getBucketName())
					.prefix(key).delimiter(delimiter);
			if (maxKeys != null) {
				requestBuilder.maxKeys(maxKeys);
			}
			if (nextToken != null) {
				requestBuilder.continuationToken(nextToken);
			}
			ListObjectsV2Response objectListing = s3AsyncClient.listObjectsV2(requestBuilder.build()).get();

			StorageFileInfoListing listing = new StorageFileInfoListing()
					.setNextToken(objectListing.nextContinuationToken());
			listing.setMaxKeys(objectListing.maxKeys());
			listing.setEncodingType(objectListing.encodingTypeAsString());
			listing.setPrefix(objectListing.prefix());
			List<S3Object> summaryList = objectListing.contents();
			if (CollectionUtils.isEmpty(summaryList)) {
				return listing;
			}
			String host = null;
			for (S3Object summary : summaryList) {
				if (key.equals(summary.key())) {
					continue;
				}
				StorageFileInfo storageFileInfo = new StorageFileInfo();
				if (host == null) {
					host = formatHost();
				}
				storageFileInfo.setHost(host);
				storageFileInfo.setKey(summary.key());
				storageFileInfo.setBucketName(config.getBucketName());
				storageFileInfo
						.setLastModified(summary.lastModified() != null ? summary.lastModified().toEpochMilli() : null);
				storageFileInfo.setETag(summary.eTag());
				storageFileInfo.setSize(summary.size());
				storageFileInfo.setOwner(summary.owner() != null ? summary.owner().id() : null);
				listing.addFileInfo(storageFileInfo);
			}
			return listing;

		}
		catch (S3Exception s3Exception) {
			throw throwS3Exception(s3Exception);
		}
		catch (Exception e) {
			throw new StorageException("0002", e);
		}
	}

	@Override
	public StorageFileInfo putObject(String key, File file, Map<String, String> userMetaData,
			Consumer<StorageProgressEvent> consumer) {
		return putObjectCommon(key, file, null, userMetaData, consumer);
	}

	@Override
	public StorageFileInfo putObject(String key, InputStream inputStream, String contentType,
			Map<String, String> userMetaData, Consumer<StorageProgressEvent> consumer) {
		return putObjectCommon(key, inputStream, contentType, userMetaData, consumer);
	}

	@Override
	public StorageFileInfo putObject(String key, byte[] bytes, String contentType, Map<String, String> userMetaData,
			Consumer<StorageProgressEvent> consumer) {
		return putObjectCommon(key, bytes, contentType, userMetaData, consumer);
	}

	StorageFileInfo putObjectCommon(String key, Object in, String contentType, Map<String, String> userMetaData,
			Consumer<StorageProgressEvent> consumer) {
		key = formatKey(key, false);
		try (S3AsyncClient s3AsyncClient = getClient(key, "putObject")) {

			PutObjectRequest.Builder requestBuilder = PutObjectRequest.builder().bucket(config.getBucketName())
					.key(key);
			AsyncRequestBody asyncRequestBody = null;
			if (in instanceof File) {
				File file = (File) in;
				asyncRequestBody = AsyncRequestBody.fromFile(file);
				if (StringUtils.isBlank(contentType)) {
					contentType = Mimetype.getInstance().getMimetype(file);
				}
			}
			else if (in instanceof InputStream) {
				asyncRequestBody = AsyncRequestBody.fromBytes(IoUtils.toByteArray((InputStream) in));
			}
			else if (in instanceof byte[]) {
				asyncRequestBody = AsyncRequestBody.fromBytes((byte[]) in);
			}
			if (asyncRequestBody == null) {
				throw new StorageException("0003", "不支持的形式");
			}
			if (userMetaData == null) {
				userMetaData = new HashMap<>(1);
			}
			userMetaData.put("upclient", "storage");
			requestBuilder.metadata(userMetaData);

			PutObjectResponse putObjectResponse = s3AsyncClient.putObject(requestBuilder.build(), asyncRequestBody)
					.get();
			StorageFileInfo storageFileInfo = new StorageFileInfo().setBucketName(config.getBucketName()).setKey(key)
					.setHost(formatHost()).setETag(putObjectResponse.eTag());
			return storageFileInfo;
		}
		catch (S3Exception s3Exception) {
			throw throwS3Exception(s3Exception);
		}
		catch (Exception e) {
			if (e instanceof StorageException) {
				StorageException e1 = (StorageException) e;
				throw new StorageException(e1.getErrcode(), e1.getErrmsg());
			}
			throw new StorageException("0002", e);
		}
	}

	@Override
	public StorageFileInfo downloadStream(String key, Consumer<StorageProgressEvent> consumer) {
		key = formatKey(key, false);
		try (S3AsyncClient s3AsyncClient = getClient(key, "downloadObject")) {
			GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(config.getBucketName()).key(key)
					.build();
			ResponseBytes<GetObjectResponse> getObjectResponseResponseBytes = s3AsyncClient
					.getObject(getObjectRequest, AsyncResponseTransformer.toBytes()).get();
			StorageFileInfo storageFileInfo = new StorageFileInfo().setBucketName(config.getBucketName()).setKey(key)
					.setHost(formatHost()).setContent(getObjectResponseResponseBytes.asInputStream());
			GetObjectResponse response = getObjectResponseResponseBytes.response();
			Map<String, String> metaData = response.metadata();
			if (response.hasMetadata()) {
				storageFileInfo.setMetaData(new HashMap<>(response.metadata()));
			}
			storageFileInfo.setETag(response.eTag())
					.setLastModified(response.lastModified() == null ? null : response.lastModified().toEpochMilli())
					.setSize(response.contentLength());
			return storageFileInfo;
		}
		catch (S3Exception s3Exception) {
			throw throwS3Exception(s3Exception);
		}
		catch (Exception e) {
			throw new StorageDownloadException("0002", e);
		}
	}

	@Override
	public byte[] download(String key, Consumer<StorageProgressEvent> consumer) {
		key = formatKey(key, false);
		try (S3AsyncClient s3AsyncClient = getClient(key, "downloadObject")) {
			GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(config.getBucketName()).key(key)
					.build();
			ResponseBytes<GetObjectResponse> getObjectResponseResponseBytes = s3AsyncClient
					.getObject(getObjectRequest, AsyncResponseTransformer.toBytes()).get();
			return getObjectResponseResponseBytes.asByteArray();
		}
		catch (S3Exception s3Exception) {
			throw throwS3Exception(s3Exception);
		}
		catch (Exception e) {
			throw new StorageDownloadException("0002", e);
		}
	}

	@Override
	public File download(String key, File destFile, Consumer<StorageProgressEvent> consumer) {
		key = formatKey(key, false);
		try (S3AsyncClient s3AsyncClient = getClient(key, "downloadObject")) {
			GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(config.getBucketName()).key(key)
					.build();
			GetObjectResponse getObjectResponse = s3AsyncClient
					.getObject(getObjectRequest, AsyncResponseTransformer.toFile(destFile)).get();
			return destFile;
		}
		catch (S3Exception s3Exception) {
			throw throwS3Exception(s3Exception);
		}
		catch (Exception e) {
			throw new StorageDownloadException("0002", e);
		}
	}

	@Override
	public StorageFileInfo copyObject(String sourceKeyWithoutBasePath, String destinationKeyWithoutBasePath,
			Map<String, String> userMetaData) {
		try (S3AsyncClient s3AsyncClient = getClient(sourceKeyWithoutBasePath, "copyObject")) {
			if (userMetaData == null) {
				userMetaData = new HashMap<>(8);
			}
			userMetaData.put("upclient", "storage");
			CopyObjectRequest request = CopyObjectRequest.builder().sourceBucket(config.getBucketName())
					.sourceKey(sourceKeyWithoutBasePath).destinationBucket(config.getBucketName())
					.destinationKey(destinationKeyWithoutBasePath).metadata(userMetaData).build();
			CopyObjectResponse copyObjectResponse = s3AsyncClient.copyObject(request).get();
			CopyObjectResult copyObjectResult = copyObjectResponse.copyObjectResult();
			return new StorageFileInfo().setBucketName(config.getBucketName()).setKey(destinationKeyWithoutBasePath)
					.setHost(formatHost()).setETag(copyObjectResult.eTag())
					.setLastModified(copyObjectResult.lastModified().toEpochMilli());
		}
		catch (S3Exception s3Exception) {
			throw throwS3Exception(s3Exception);
		}
		catch (Exception e) {
			throw new StorageException("0002", e);
		}
	}

	@Override
	public void deleteObject(String key) {
		key = formatKey(key, false);
		try (S3AsyncClient s3AsyncClient = getClient(key, "deleteObject")) {
			DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(config.getBucketName())
					.key(key).build();
			DeleteObjectResponse deleteObjectResponse = s3AsyncClient.deleteObject(deleteObjectRequest).get();
		}
		catch (S3Exception s3Exception) {
			throw throwS3Exception(s3Exception);
		}
		catch (Exception e) {
			throw new StorageException("0002", e);
		}
	}

	@Override
	public StorageSignature generateWebUploadSign(String key, String mimeType, String successActionStatus) {
		key = formatKey(key, false);
		return generateSignagure(config, key, mimeType, successActionStatus);
	}

	@Override
	public URL generatePresignedUrl(String key, Date expiration) {
		key = formatKey(key, false);
		try (S3AsyncClient s3AsyncClient = getClient(key, "generatePresignedUrl")) {
			GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(config.getBucketName()).key(key)
					.build();

			GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
					.getObjectRequest(getObjectRequest).signatureDuration(Duration.ofMinutes(15)).build();
			S3Presigner s3Presigner = S3Presigner.builder()
					// .credentialsProvider()
					.build();
			PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);

			return presignedGetObjectRequest.url();
		}
		catch (S3Exception s3Exception) {
			throw throwS3Exception(s3Exception);
		}
		catch (Exception e) {
			throw new StorageException("0002", e);
		}
	}

	StorageException throwS3Exception(S3Exception s3Exception) {
		AwsErrorDetails awsErrorDetails = s3Exception.awsErrorDetails();
		if (awsErrorDetails != null) {
			return new StorageException(awsErrorDetails.errorCode(), awsErrorDetails.errorMessage());
		}
		return new StorageException(s3Exception.requestId(), s3Exception);
	}

	@Override
	public void shutdown() {
		STS_TOKEN_CACHE.cleanUp();
	}

	private String formatHost() {
		return StorageUtil.formatHost(config);
	}

	private String formatKey(String key, boolean isDir) {
		return StorageUtil.formatKey(config.getBasePath(), key, isDir);
	}

	protected StorageStsToken requestSTSToken(StorageClientConfig config, String key, String action) {

		return StorageUtil.requestSTSToken(HttpUtil::doPostJson, config, key, action);
	}

	protected StorageSignature requestSign(StorageClientConfig config, String key, String mimeType,
			String successActionStatus) {
		return StorageUtil.requestSign(HttpUtil::doPostJson, config, key, mimeType, successActionStatus);
	}

}
