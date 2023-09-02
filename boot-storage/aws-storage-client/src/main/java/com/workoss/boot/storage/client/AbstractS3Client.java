/*
 * Copyright 2019-2023 workoss (https://www.workoss.com)
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

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.workoss.boot.storage.config.StorageClientConfig;
import com.workoss.boot.storage.exception.StorageClientNotFoundException;
import com.workoss.boot.storage.exception.StorageDownloadException;
import com.workoss.boot.storage.exception.StorageException;
import com.workoss.boot.storage.model.*;
import com.workoss.boot.storage.util.HttpUtil;
import com.workoss.boot.storage.util.StorageUtil;
import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.collection.CollectionUtils;
import com.workoss.boot.util.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	protected static AmazonS3ClientBuilder S3_CLIENT_BUILDER;

	protected static Cache<String, StorageStsToken> STS_TOKEN_CACHE = Caffeine.newBuilder()
		.expireAfterWrite(12, TimeUnit.MINUTES)
		.maximumSize(200)
		.removalListener((String key, StorageStsToken value, RemovalCause cause) -> {
			log.debug("【STORAGE】STS_TOKEN_CACHE KEY：{} cause:{}", key, cause);
		})
		.build();

	@Override
	public void init(StorageClientConfig config) {
		this.config = config;
		// 各客户端初始化
		this.initConfig(config);
		this.S3_CLIENT_BUILDER = initS3ClientBuilder();
	}

	/**
	 * 客户端初始化配置
	 * @param config 配置信息
	 */
	protected abstract void initConfig(StorageClientConfig config);

	protected AmazonS3ClientBuilder initS3ClientBuilder() {
		return AmazonS3ClientBuilder.standard().withPathStyleAccessEnabled(false);
	}

	/**
	 * 创建s3客户端
	 * @param config config
	 * @param stsToken stsToken
	 * @return s3客户端
	 */
	protected abstract AmazonS3 createClient(StorageClientConfig config, StorageStsToken stsToken);

	protected AmazonS3 createS3Client(StorageClientConfig config, String endpoint, StorageStsToken stsToken) {
		AWSCredentials credentials = null;
		if (stsToken == null) {
			credentials = new BasicAWSCredentials(config.getAccessKey(), config.getSecretKey());
		}
		else {
			credentials = new BasicSessionCredentials(stsToken.getAccessKey(), stsToken.getSecretKey(),
					stsToken.getStsToken());
		}
		return (S3_CLIENT_BUILDER != null ? initS3ClientBuilder() : S3_CLIENT_BUILDER)
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.withEndpointConfiguration(
					new AwsClientBuilder.EndpointConfiguration(endpoint, type().name().toLowerCase()))
			.build();
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

	protected AmazonS3 getClient(String key, String action) {
		if (!useStsToken()) {
			return createClient(config, null);
		}
		if (!useStsToken()) {
			throw new StorageClientNotFoundException("00002",
					"Storage action:" + action + " key:" + key + " initClient error");
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
		AmazonS3 amazonS3 = getClient("", "listBuckets");
		try {
			ListBucketsRequest listBucketsRequest = new ListBucketsRequest();
			List<Bucket> buckets = amazonS3.listBuckets();
			if (CollectionUtils.isEmpty(buckets)) {
				return null;
			}
			return buckets.stream()
				.map(bucket -> new StorageBucketInfo(bucket.getName(),
						bucket.getOwner() == null ? null : bucket.getOwner().getId(), bucket.getCreationDate()))
				.collect(Collectors.toList());
		}
		catch (AmazonS3Exception e) {
			throw new StorageException(e.getErrorCode(), e.getMessage());
		}
		catch (Exception e) {
			throw new StorageException("0002", ExceptionUtils.toShortString(e, 2));
		}
		finally {
			amazonS3.shutdown();
		}
	}

	@Override
	public StorageBucketInfo getBucket() {
		AmazonS3 amazonS3 = getClient("", "getBucket");
		try {
			ListBucketsRequest listBucketsRequest = new ListBucketsRequest();
			List<Bucket> buckets = amazonS3.listBuckets(listBucketsRequest);
			if (CollectionUtils.isEmpty(buckets)) {
				return null;
			}
			return buckets.stream()
				.filter(bucket -> config.getBucketName().equals(bucket.getName()))
				.map(bucket -> new StorageBucketInfo(bucket.getName(),
						bucket.getOwner() == null ? null : bucket.getOwner().getId(), bucket.getCreationDate()))
				.findFirst()
				.orElseGet(() -> null);
		}
		catch (AmazonS3Exception e) {
			throw new StorageException(e.getErrorCode(), e.getMessage());
		}
		catch (Exception e) {
			throw new StorageException("0002", ExceptionUtils.toShortString(e, 2));
		}
		finally {
			amazonS3.shutdown();
		}
	}

	@Override
	public boolean doesBucketExist() {
		AmazonS3 amazonS3 = getClient("", "doesBucketExist");
		try {
			return amazonS3.doesBucketExistV2(config.getBucketName());
		}
		catch (AmazonS3Exception e) {
			throw new StorageException(e.getErrorCode(), e.getMessage());
		}
		catch (Exception e) {
			throw new StorageException("0002", ExceptionUtils.toShortString(e, 2));
		}
		finally {
			amazonS3.shutdown();
		}
	}

	@Override
	public boolean doesObjectExist(String key) {
		AmazonS3 amazonS3 = getClient(key, "doesObjectExist");
		try {
			return amazonS3.doesObjectExist(config.getBucketName(), formatKey(key, false));
		}
		catch (AmazonS3Exception e) {
			throw new StorageException(e.getErrorCode(), e.getMessage());
		}
		catch (Exception e) {
			throw new StorageException("0002", ExceptionUtils.toShortString(e, 2));
		}
		finally {
			amazonS3.shutdown();
		}
	}

	@Override
	public StorageFileInfo getObject(String key) {
		key = formatKey(key, false);
		AmazonS3 amazonS3 = getClient(key, "getObject");
		try {
			GetObjectMetadataRequest request = new GetObjectMetadataRequest(config.getBucketName(), key);
			ObjectMetadata objectMetadata = amazonS3.getObjectMetadata(request);
			if (objectMetadata == null) {
				return null;
			}
			StorageFileInfo storageFileInfo = new StorageFileInfo();
			storageFileInfo.setBucketName(config.getBucketName());
			storageFileInfo.setKey(key);
			storageFileInfo.setHost(formatHost());
			Map<String, Object> metaData = objectMetadata.getRawMetadata();
			storageFileInfo.setMetaData(metaData);
			storageFileInfo.setETag((String) metaData.get("ETag"));
			Date expireDate = (Date) metaData.get("Last-Modified");
			storageFileInfo.setLastModified(expireDate != null ? expireDate.getTime() : null);
			storageFileInfo.setSize(objectMetadata.getContentLength());

			return storageFileInfo;
		}
		catch (AmazonS3Exception e) {
			throw new StorageException(e.getErrorCode(), e.getMessage());
		}
		catch (Exception e) {
			throw new StorageException("0002", ExceptionUtils.toShortString(e, 2));
		}
		finally {
			amazonS3.shutdown();
		}
	}

	@Override
	public StorageFileInfoListing listObjects(String key, String delimiter, String nextToken, Integer maxKeys) {
		key = formatKey(key, true);
		AmazonS3 amazonS3 = getClient(key, "listObjects");
		try {
			ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request().withBucketName(config.getBucketName())
				.withPrefix(key);
			if (StringUtils.isNotBlank(delimiter)) {
				listObjectsRequest.setDelimiter(delimiter);
			}
			if (StringUtils.isNotBlank(nextToken)) {
				listObjectsRequest.setContinuationToken(nextToken);
			}
			if (maxKeys != null) {
				listObjectsRequest.setMaxKeys(maxKeys);
			}
			ListObjectsV2Result objectListing = amazonS3.listObjectsV2(listObjectsRequest);
			if (objectListing == null) {
				return null;
			}
			StorageFileInfoListing listing = new StorageFileInfoListing();
			listing.setNextToken(objectListing.getNextContinuationToken());
			listing.setMaxKeys(objectListing.getMaxKeys());
			listing.setEncodingType(objectListing.getEncodingType());
			listing.setPrefix(objectListing.getPrefix());
			List<S3ObjectSummary> summaryList = objectListing.getObjectSummaries();
			if (CollectionUtils.isEmpty(summaryList)) {
				return listing;
			}
			String host = null;
			for (S3ObjectSummary summary : summaryList) {
				if (key.equals(summary.getKey())) {
					continue;
				}
				StorageFileInfo storageFileInfo = new StorageFileInfo();
				if (host == null) {
					host = formatHost();
				}
				storageFileInfo.setHost(host);
				storageFileInfo.setKey(summary.getKey());
				storageFileInfo.setBucketName(config.getBucketName());
				storageFileInfo
					.setLastModified(summary.getLastModified() != null ? summary.getLastModified().getTime() : null);
				storageFileInfo.setETag(summary.getETag());
				storageFileInfo.setSize(summary.getSize());
				storageFileInfo.setOwner(summary.getOwner() != null ? summary.getOwner().getId() : null);
				listing.addFileInfo(storageFileInfo);
			}
			return listing;
		}
		catch (AmazonS3Exception e) {
			throw new StorageException(e.getErrorCode(), e.getMessage());
		}
		catch (Exception e) {
			throw new StorageException("0002", ExceptionUtils.toShortString(e, 2));
		}
		finally {
			amazonS3.shutdown();
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
		AmazonS3 amazonS3 = getClient(key, "putObject");
		try {
			PutObjectRequest putObjectRequest = null;
			ObjectMetadata objectMetadata = new ObjectMetadata();
			if (StringUtils.isBlank(contentType)) {
				contentType = StorageUtil.getMimeType(key);
			}
			if (StringUtils.isNotBlank(contentType)) {
				objectMetadata.setContentType(contentType);
			}
			if (in instanceof File file) {
				putObjectRequest = new PutObjectRequest(config.getBucketName(), key, file);
			}
			else if (in instanceof InputStream inputStream) {
				// 根据文件名称 放入objectMetaData
				putObjectRequest = new PutObjectRequest(config.getBucketName(), key, inputStream, objectMetadata);
			}
			else if (in instanceof byte[] bytes) {
				InputStream is = new ByteArrayInputStream(bytes);
				putObjectRequest = new PutObjectRequest(config.getBucketName(), key, is, objectMetadata);
			}

			if (consumer != null) {
				ProgressListener progressListener = progressEvent -> consumer
					.accept(new StorageProgressEvent(progressEvent.getBytes(), progressEvent.getEventType().name(),
							progressEvent.getBytesTransferred()));
				putObjectRequest.setGeneralProgressListener(progressListener);
			}
			if (userMetaData == null) {
				userMetaData = new HashMap<>(1);
			}
			userMetaData.put("upclient", "storage");
			userMetaData.entrySet()
				.stream()
				.forEach(stringStringEntry -> objectMetadata.addUserMetadata(stringStringEntry.getKey(),
						stringStringEntry.getValue()));
			putObjectRequest.setMetadata(objectMetadata);
			PutObjectResult putObjectResult = amazonS3.putObject(putObjectRequest);
			StorageFileInfo storageFileInfo = new StorageFileInfo();
			storageFileInfo.setKey(key);
			storageFileInfo.setBucketName(config.getBucketName());
			storageFileInfo.setHost(formatHost());
			storageFileInfo.setETag(putObjectResult.getETag());
			ObjectMetadata metadata = putObjectResult.getMetadata();
			if (metadata != null) {
				storageFileInfo
					.setLastModified(metadata.getLastModified() != null ? metadata.getLastModified().getTime() : null);
				storageFileInfo.setMetaData(metadata.getRawMetadata());
			}
			return storageFileInfo;
		}
		catch (AmazonS3Exception e) {
			throw new StorageException(e.getErrorCode(), e.getMessage());
		}
		catch (Exception e) {
			throw new StorageException("0002", ExceptionUtils.toShortString(e, 2));
		}
		finally {
			amazonS3.shutdown();
		}
	}

	@Override
	public StorageFileInfo downloadStream(String key, Consumer<StorageProgressEvent> consumer) {
		key = formatKey(key, false);
		AmazonS3 amazonS3 = getClient(key, "downloadObject");
		try {
			GetObjectRequest getObjectRequest = new GetObjectRequest(config.getBucketName(), key);
			if (consumer != null) {
				ProgressListener progressListener = progressEvent -> consumer
					.accept(new StorageProgressEvent(progressEvent.getBytes(), progressEvent.getEventType().name(),
							progressEvent.getBytesTransferred()));
				getObjectRequest.setGeneralProgressListener(progressListener);
			}
			S3Object s3Object = amazonS3.getObject(getObjectRequest);
			if (s3Object == null) {
				return null;
			}
			StorageFileInfo storageFileInfo = new StorageFileInfo().setBucketName(config.getBucketName())
				.setKey(key)
				.setHost(formatHost())
				.setContent(s3Object.getObjectContent());
			ObjectMetadata objectMetadata = s3Object.getObjectMetadata();
			if (objectMetadata != null) {
				storageFileInfo.setSize(objectMetadata.getContentLength())
					.setMetaData(objectMetadata.getRawMetadata())
					.setETag(objectMetadata.getETag())
					.setLastModified(objectMetadata.getLastModified() == null ? null
							: objectMetadata.getLastModified().getTime());
			}
			return storageFileInfo;
		}
		catch (AmazonS3Exception e) {
			throw new StorageException(e.getErrorCode(), e.getMessage());
		}
		catch (Exception e) {
			throw new StorageDownloadException("0002", ExceptionUtils.toShortString(e, 2));
		}
		finally {
			amazonS3.shutdown();
		}
	}

	@Override
	public byte[] download(String key, Consumer<StorageProgressEvent> consumer) {
		StorageFileInfo storageFileInfo = downloadStream(key, consumer);
		if (storageFileInfo == null || storageFileInfo.getContent() == null) {
			return null;
		}
		try (InputStream inputStream = storageFileInfo.getContent()) {
			return IOUtils.toByteArray(inputStream);
		}
		catch (AmazonS3Exception e) {
			throw new StorageException(e.getErrorCode(), e.getMessage());
		}
		catch (Exception e) {
			throw new StorageDownloadException("0002", ExceptionUtils.toShortString(e, 2));
		}
	}

	@Override
	public File download(String key, File destFile, Consumer<StorageProgressEvent> consumer) {
		key = formatKey(key, false);
		AmazonS3 amazonS3 = getClient(key, "downloadObject");
		try {
			GetObjectRequest getObjectRequest = new GetObjectRequest(config.getBucketName(), key);
			if (consumer != null) {
				ProgressListener progressListener = progressEvent -> consumer
					.accept(new StorageProgressEvent(progressEvent.getBytes(), progressEvent.getEventType().name(),
							progressEvent.getBytesTransferred()));
				getObjectRequest.setGeneralProgressListener(progressListener);
			}
			ObjectMetadata objectMetadata = amazonS3.getObject(getObjectRequest, destFile);
			if (objectMetadata == null) {
				return null;
			}
			return destFile;
		}
		catch (AmazonS3Exception e) {
			throw new StorageException(e.getErrorCode(), e.getMessage());
		}
		catch (Exception e) {
			throw new StorageDownloadException("0002", ExceptionUtils.toShortString(e, 2));
		}
		finally {
			amazonS3.shutdown();
		}
	}

	@Override
	public StorageFileInfo copyObject(String sourceKeyWithoutBasePath, String destinationKeyWithoutBasePath,
			Map<String, String> userMetaData) {
		AmazonS3 amazonS3 = getClient(sourceKeyWithoutBasePath, "copyObject");
		try {
			CopyObjectRequest copyObjectRequest = new CopyObjectRequest().withSourceBucketName(config.getBucketName())
				.withDestinationBucketName(config.getBucketName())
				.withSourceKey(sourceKeyWithoutBasePath)
				.withDestinationKey(destinationKeyWithoutBasePath);
			if (userMetaData == null) {
				userMetaData = new HashMap<>(1);
			}
			ObjectMetadata newObjectMetadata = new ObjectMetadata();
			userMetaData.put("upclient", "storage");
			userMetaData.entrySet()
				.stream()
				.forEach(stringStringEntry -> newObjectMetadata.addUserMetadata(stringStringEntry.getKey(),
						stringStringEntry.getValue()));
			copyObjectRequest.setNewObjectMetadata(newObjectMetadata);
			CopyObjectResult copyObjectResult = amazonS3.copyObject(copyObjectRequest);
			StorageFileInfo storageFileInfo = new StorageFileInfo();
			storageFileInfo.setETag(copyObjectResult.getETag());
			storageFileInfo.setLastModified(copyObjectResult.getLastModifiedDate() != null
					? copyObjectResult.getLastModifiedDate().getTime() : null);
			return storageFileInfo;
		}
		catch (AmazonS3Exception e) {
			throw new StorageException(e.getErrorCode(), e.getMessage());
		}
		catch (Exception e) {
			throw new StorageException("0002", ExceptionUtils.toShortString(e, 2));
		}
		finally {
			amazonS3.shutdown();
		}
	}

	@Override
	public void deleteObject(String key) {
		key = formatKey(key, false);
		AmazonS3 amazonS3 = getClient(key, "deleteObject");
		try {
			DeleteObjectRequest request = new DeleteObjectRequest(config.getBucketName(), key);
			amazonS3.deleteObject(request);
		}
		catch (AmazonS3Exception e) {
			throw new StorageException(e.getErrorCode(), e.getMessage());
		}
		catch (Exception e) {
			throw new StorageException("0002", ExceptionUtils.toShortString(e, 2));
		}
		finally {
			amazonS3.shutdown();
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
		// 不能用 sts 方式获取 没权限
		AmazonS3 amazonS3 = getClient(key, "generatePresignedUrl");
		try {
			GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(config.getBucketName(), key);
			request.setSSEAlgorithm(SSEAlgorithm.AES256);
			request.setExpiration(expiration);
			return amazonS3.generatePresignedUrl(request);
		}
		catch (AmazonS3Exception e) {
			throw new StorageException(e.getErrorCode(), e.getMessage());
		}
		catch (Exception e) {
			throw new StorageException("0002", ExceptionUtils.toShortString(e, 2));
		}
		finally {
			amazonS3.shutdown();
		}
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
