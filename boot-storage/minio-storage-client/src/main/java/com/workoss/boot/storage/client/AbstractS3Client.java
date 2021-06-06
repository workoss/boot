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
import com.workoss.boot.storage.exception.StorageClientNotFoundException;
import com.workoss.boot.storage.exception.StorageDownloadException;
import com.workoss.boot.storage.exception.StorageException;
import com.workoss.boot.storage.model.*;
import com.workoss.boot.storage.util.HttpUtil;
import com.workoss.boot.storage.util.StorageUtil;
import com.workoss.boot.util.DateUtils;
import com.workoss.boot.util.StreamUtils;
import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.collection.CollectionUtils;
import io.minio.*;
import io.minio.credentials.Provider;
import io.minio.credentials.StaticProvider;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import io.minio.messages.Owner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
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
 * minio abstract
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
public abstract class AbstractS3Client implements StorageClient {

	private static final Logger log = LoggerFactory.getLogger(AbstractS3Client.class);

	protected MinioClient minioClient;

	protected StorageClientConfig config;

	protected static Cache<String, StorageStsToken> STS_TOKEN_CACHE = Caffeine.newBuilder()
			.expireAfterWrite(12, TimeUnit.MINUTES).maximumSize(200)
			.removalListener((String key, StorageStsToken value, RemovalCause cause) -> {
				log.debug("【STORAGE】STS_TOKEN_CACHE KEY：{} cause:{}", key, cause);
			}).build();

	@Override
	public void init(StorageClientConfig config) {
		this.config = config;

	}

	/**
	 * 客户端初始化
	 *
	 * @param config 配置
	 */
	protected abstract void initConfig(StorageClientConfig config);

	/**
	 * 创建minio 客户端
	 *
	 * @param config   配置
	 * @param stsToken stsToken
	 * @return minio客户端
	 */
	protected abstract MinioClient createClient(StorageClientConfig config, StorageStsToken stsToken);

	protected MinioClient createS3Client(StorageClientConfig config, String endpoint, StorageStsToken stsToken) {
		Provider provider = null;
		if (stsToken == null) {
			provider = new StaticProvider(config.getAccessKey(), config.getSecretKey(), null);
		} else {
			provider = new StaticProvider(stsToken.getAccessKey(), stsToken.getSecretKey(), stsToken.getStsToken());
		}
		return MinioClient.builder().endpoint(endpoint).credentialsProvider(provider)
				.region(type().name().toLowerCase()).build();
	}

	@Override
	public boolean doesBucketExist() {
		MinioClient minioClient = getClient("", "doesBucketExist");
		try {
			return minioClient.bucketExists(BucketExistsArgs.builder().bucket(config.getBucketName()).build());
		} catch (Exception e) {
			throw new StorageException("0002", e);
		}
	}

	@Override
	public StorageBucketInfo getBucket() {
		MinioClient minioClient = getClient("", "getBucket");
		try {
			List<Bucket> buckets = minioClient.listBuckets();
			if (CollectionUtils.isEmpty(buckets)) {
				return null;
			}
			return buckets.stream().filter(bucket -> config.getBucketName().equals(bucket.name()))
					.map(bucket -> new StorageBucketInfo(bucket.name(), null,
							DateUtils.toDate(bucket.creationDate().toLocalDateTime())))
					.findFirst().orElseGet(() -> null);
		} catch (Exception e) {
			throw new StorageException("0002", e);
		}
	}

	@Override
	public List<StorageBucketInfo> listBuckets() {
		MinioClient minioClient = getClient("", "listBuckets");
		try {
			List<Bucket> buckets = minioClient.listBuckets();
			if (CollectionUtils.isEmpty(buckets)) {
				return null;
			}
			return buckets.stream().map(bucket -> new StorageBucketInfo(bucket.name(), null,
					DateUtils.toDate(bucket.creationDate().toLocalDateTime()))).collect(Collectors.toList());
		} catch (Exception e) {
			throw new StorageException("0002", e);
		}
	}

	@Override
	public boolean doesObjectExist(String key) {
		key = formatKey(key, false);
		MinioClient minioClient = getClient(key, "doesObjectExist");
		try {
			StatObjectResponse response = minioClient
					.statObject(StatObjectArgs.builder().bucket(config.getBucketName()).object(key).build());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public StorageFileInfo getObject(String key) {
		key = formatKey(key, false);
		MinioClient minioClient = getClient(key, "getObject");
		try {
			StatObjectResponse response = minioClient
					.statObject(StatObjectArgs.builder().bucket(config.getBucketName()).object(key).build());
			StorageFileInfo storageFileInfo = new StorageFileInfo();
			storageFileInfo.setBucketName(response.bucket());
			storageFileInfo.setKey(key);
			storageFileInfo.setHost(formatHost());
			storageFileInfo.setSize(response.size());
			storageFileInfo.setLastModified(DateUtils.getMillis(response.lastModified().toLocalDateTime()));
			Map<String, Object> userMeta = new HashMap<>();
			userMeta.putAll(response.userMetadata());
			storageFileInfo.setMetaData(userMeta);
			storageFileInfo.setETag(response.etag());
			return storageFileInfo;
		} catch (Exception e) {
			throw new StorageException("0002", e);
		}
	}

	@Override
	public StorageFileInfoListing listObjects(String key, String delimiter, String nextToken, Integer maxKey) {
		key = formatKey(key, true);
		MinioClient minioClient = getClient(key, "listObjects");
		boolean isFile = StorageUtil.SLASH.equals(delimiter);
		try {
			ListObjectsArgs.Builder builder = ListObjectsArgs.builder().bucket(config.getBucketName()).maxKeys(maxKey)
					.delimiter(delimiter).includeUserMetadata(true);
			if (StringUtils.isNotBlank(nextToken)) {
				// 报错
				// builder.continuationToken(nextToken);
			}
			Iterable<Result<Item>> listObjects = minioClient.listObjects(builder.build());
			StorageFileInfoListing listing = new StorageFileInfoListing();
			for (Result<Item> listObject : listObjects) {
				Item item = listObject.get();
				StorageFileInfo fileInfo = new StorageFileInfo();
				fileInfo.setBucketName(config.getBucketName());
				fileInfo.setKey(item.objectName());
				if (!item.isDir()) {
					Owner owner = item.owner();
					if (owner != null && owner.id() != null) {
						fileInfo.setOwner(owner.id());
					}
					fileInfo.setSize(item.size());
					if (item.lastModified() != null) {
						fileInfo.setLastModified(DateUtils.getMillis(item.lastModified().toLocalDateTime()));
					}
					fileInfo.setHost(formatHost());
					Map<String, String> userMeta = item.userMetadata();
					if (CollectionUtils.isNotEmpty(userMeta)) {
						Map<String, Object> userMetaMap = new HashMap<>();
						userMetaMap.putAll(userMeta);
						fileInfo.setMetaData(userMetaMap);
					}
				}
				if (isFile == item.isDir()) {
					continue;
				}
				listing.addFileInfo(fileInfo);
			}
			return listing;
		} catch (Exception e) {
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
	public StorageFileInfo putObject(String key, byte[] bytes, String contentType,
									 Map<String, String> userMetaData, Consumer<StorageProgressEvent> consumer) {
		return putObjectCommon(key, bytes, contentType, userMetaData, consumer);
	}

	StorageFileInfo putObjectCommon(String key, Object in, String contentType, Map<String, String> userMetaData,
									Consumer<StorageProgressEvent> consumer) {
		key = formatKey(key, false);
		MinioClient minioClient = getClient(key, "putObject");
		InputStream inputStream = null;
		try {
			PutObjectArgs.Builder builder = PutObjectArgs.builder().bucket(config.getBucketName()).object(key);
			if (in instanceof File) {
				inputStream = new FileInputStream((File) in);
			} else if (in instanceof InputStream) {
				// 根据文件名称 放入objectMetaData
				inputStream = (InputStream) in;
			} else if (in instanceof byte[]) {
				inputStream = new ByteArrayInputStream((byte[]) in);
			}
			builder.stream(inputStream, inputStream.available(), -1);
			if (StringUtils.isNotBlank(contentType)) {
				builder.contentType(contentType);
			}
			if (userMetaData == null) {
				userMetaData = new HashMap<>(1);
			}
			userMetaData.put("upclient", "storage");
			builder.userMetadata(userMetaData);
			ObjectWriteResponse objectWriteResponse = minioClient.putObject(builder.build());
			StorageFileInfo fileInfo = new StorageFileInfo();
			fileInfo.setBucketName(config.getBucketName());
			fileInfo.setKey(objectWriteResponse.object());
			fileInfo.setETag(objectWriteResponse.etag());
			fileInfo.setHost(formatHost());
			return fileInfo;
		} catch (Exception e) {
			throw new StorageException("0002", e);
		} finally {
			StreamUtils.close(inputStream);
		}
	}

	@Override
	public StorageFileInfo downloadWithSign(String key, Consumer<StorageProgressEvent> consumer) {
		throw new RuntimeException("not support");
	}

	@Override
	public InputStream downloadStream(String key, Consumer<StorageProgressEvent> consumer) {
		key = formatKey(key, false);
		MinioClient minioClient = getClient(key, "downloadObject");
		try {
			GetObjectResponse objectResponse = minioClient
					.getObject(GetObjectArgs.builder().bucket(config.getBucketName()).object(key).build());
			return objectResponse;
		} catch (Exception e) {
			throw new StorageException("0002", e);
		}
	}

	@Override
	public byte[] download(String key, Consumer<StorageProgressEvent> consumer) {
		try (InputStream inputStream = downloadStream(key, consumer)) {
			return StreamUtils.copyToByteArray(inputStream);
		} catch (Exception e) {
			throw new StorageDownloadException("0002", e);
		}
	}

	@Override
	public File download(String key, File destFile, Consumer<StorageProgressEvent> consumer) {
		key = formatKey(key, false);
		MinioClient minioClient = getClient(key, "downloadObject");
		try {
			minioClient.downloadObject(DownloadObjectArgs.builder().bucket(config.getBucketName()).object(key)
					.filename(destFile.getPath()).build());
			return destFile;
		} catch (Exception e) {
			throw new StorageDownloadException("0002", e);
		}
	}

	@Override
	public StorageFileInfo copyObject(String sourceKeyWithoutBasePath, String destinationKeyWithoutBasePath,
									  Map<String, String> userMetaData) {
		MinioClient minioClient = getClient(sourceKeyWithoutBasePath, "copyObject");
		try {
			if (userMetaData == null) {
				userMetaData = new HashMap<>(1);
			}
			userMetaData.put("upclient", "storage");
			CopyObjectArgs copyObjectArgs = CopyObjectArgs.builder().bucket(config.getBucketName())
					.object(destinationKeyWithoutBasePath).userMetadata(userMetaData).source(CopySource.builder()
							.bucket(config.getBucketName()).object(sourceKeyWithoutBasePath).build())
					.build();
			ObjectWriteResponse response = minioClient.copyObject(copyObjectArgs);
			StorageFileInfo fileInfo = new StorageFileInfo();
			fileInfo.setBucketName(config.getBucketName());
			fileInfo.setKey(response.object());
			fileInfo.setETag(response.etag());
			fileInfo.setHost(formatHost());
			return fileInfo;
		} catch (Exception e) {
			throw new StorageDownloadException("0002", e);
		}
	}

	@Override
	public void deleteObject(String key) {
		key = formatKey(key, false);
		MinioClient minioClient = getClient(key, "deleteObject");
		try {
			minioClient.removeObject(RemoveObjectArgs.builder().bucket(config.getBucketName()).object(key).build());
		} catch (Exception e) {
			throw new StorageDownloadException("0002", e);
		}
	}

	@Override
	public StorageSignature generateWebUploadSign(String key, String mimeType,
												  String successActionStatus) {
		key = formatKey(key, false);
		return generateSignagure(config, key, mimeType, successActionStatus);
	}

	@Override
	public URL generatePresignedUrl(String key, Date expiration) {
		key = formatKey(key, false);
		MinioClient minioClient = getClient(key, "generatePresignedUrl");
		int expire = (int) (expiration.getTime() - System.currentTimeMillis());
		try {
			String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
					.bucket(config.getBucketName()).expiry(expire, TimeUnit.MILLISECONDS).object(key).build());
			return new URL(url);
		} catch (Exception e) {
			throw new StorageDownloadException("0002", e);
		}
	}

	@Override
	public void shutdown() {
		STS_TOKEN_CACHE.cleanUp();
	}

	/**
	 * 生成stsToken
	 *
	 * @param config 配置
	 * @param key    文件key
	 * @param action 操作
	 * @return
	 */
	protected abstract StorageStsToken getStsToken(StorageClientConfig config, String key, String action);

	protected abstract StorageSignature generateSignagure(StorageClientConfig config, String key, String mimeType,
														  String successActionStatus);

	protected MinioClient getClient(String key, String action) {
		if (minioClient != null) {
			return minioClient;
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

	private String formatHost() {
		return StorageUtil.formatHost(config);
	}

	private String formatKey(String key, boolean isDir) {
		return StorageUtil.formatKey(config.getBasePath(), key, isDir);
	}

	protected StorageStsToken requestSTSToken(StorageClientConfig config, String key, String action) {
		return StorageUtil.requestSTSToken(HttpUtil::executePost, config, key, action);
	}

	protected StorageSignature requestSign(StorageClientConfig config, String key, String mimeType,
										   String successActionStatus) {
		return StorageUtil.requestSign(HttpUtil::executePost, config, key, mimeType, successActionStatus);
	}

}
