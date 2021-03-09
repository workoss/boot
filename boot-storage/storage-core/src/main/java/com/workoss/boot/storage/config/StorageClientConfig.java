package com.workoss.boot.storage.config;

import com.workoss.boot.storage.model.StorageType;
import com.workoss.boot.storage.util.StorageUtil;

/**
 * 存储配置
 *
 * @author workoss
 */
public class StorageClientConfig {

	/**
	 * 存储类型 类型 默认OSS
	 */
	private StorageType storageType = StorageType.OSS;

	/**
	 * 租户ID
	 */
	private String tenentId;

	/**
	 * 桶名称
	 */
	private String bucketName;

	/**
	 * 接入点 stsToken方式下(tokenUrl有值) 可以为空
	 */
	private String endpoint;

	/**
	 * accessKey accessId aceKey
	 */
	private String accessKey;

	/**
	 * secretKey accessKeySecret
	 */
	private String secretKey;

	/**
	 * 临时授权URL ak/sk/securityToken
	 */
	private String tokenUrl;

	/**
	 * 配置的域名 没有配置 默认位 bucketName+endpoint
	 */
	private String domain;

	/**
	 * 基本目录
	 */
	private String basePath;

	public StorageType getStorageType() {
		return storageType;
	}

	public void setStorageType(StorageType storageType) {
		this.storageType = storageType;
	}

	public String getTenentId() {
		return tenentId;
	}

	public void setTenentId(String tenentId) {
		this.tenentId = tenentId;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = StorageUtil.replaceEndSlash(endpoint);
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getTokenUrl() {
		return tokenUrl;
	}

	public void setTokenUrl(String tokenUrl) {
		this.tokenUrl = tokenUrl;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		// 需要处理域名
		this.domain = StorageUtil.replaceEndSlash(domain);
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = StorageUtil.replaceStartEndSlash(basePath);
	}

}
