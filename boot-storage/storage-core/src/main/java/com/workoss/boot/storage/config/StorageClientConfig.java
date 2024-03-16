/*
 * Copyright 2019-2024 workoss (https://www.workoss.com)
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
