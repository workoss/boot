/*
 * Copyright 2019-2022 workoss (https://www.workoss.com)
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
package com.workoss.boot.storage.model;

/**
 * 上传签名
 *
 * @author workoss
 */
@SuppressWarnings("unused")
public class StorageSignature {

	private StorageType storageType;

	/**
	 * 临时accessKey
	 */
	private String accessKey;

	/**
	 * ak/sk/securityToken 签名生成
	 */
	private String stsToken;

	/**
	 * 策略
	 */
	private String policy;

	/**
	 * 签名
	 */
	private String signature;

	/**
	 * 存储key
	 */
	private String key;

	/**
	 * 域名
	 */
	private String host;

	/**
	 * 过期日期
	 */
	private Long expire;

	/**
	 * key类型
	 */
	private String mimeType;

	/**
	 * 返回状态
	 */
	private String successActionStatus;

	public StorageType getStorageType() {
		return storageType;
	}

	public void setStorageType(StorageType storageType) {
		this.storageType = storageType;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getStsToken() {
		return stsToken;
	}

	public void setStsToken(String stsToken) {
		this.stsToken = stsToken;
	}

	public String getPolicy() {
		return policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Long getExpire() {
		return expire;
	}

	public void setExpire(Long expire) {
		this.expire = expire;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getSuccessActionStatus() {
		return successActionStatus;
	}

	public void setSuccessActionStatus(String successActionStatus) {
		this.successActionStatus = successActionStatus;
	}

	@Override
	public String toString() {
		return "StorageSignature{" + "storageType=" + storageType + ", accessKey='" + accessKey + '\'' + ", stsToken='"
				+ stsToken + '\'' + ", policy='" + policy + '\'' + ", signature='" + signature + '\'' + ", key='" + key
				+ '\'' + ", host='" + host + '\'' + ", expire=" + expire + ", mimeType='" + mimeType + '\''
				+ ", successActionStatus='" + successActionStatus + '\'' + '}';
	}

}
