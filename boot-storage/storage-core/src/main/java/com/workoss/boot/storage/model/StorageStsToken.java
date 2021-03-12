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
package com.workoss.boot.storage.model;

import java.util.Date;

/**
 * sts token
 *
 * @author workoss
 */
public class StorageStsToken {

	/**
	 * accessKey accessId ackKey
	 */
	private String accessKey;

	/**
	 * secretKey accessKeySecret
	 */
	private String secretKey;

	/**
	 * 过期时间
	 */
	private Date expiration;

	/**
	 * sts token
	 */
	private String stsToken;

	/**
	 * endpoint
	 */
	private String endpoint;

	public StorageStsToken() {
	}

	public StorageStsToken(String accessKey, String secretKey, Date expiration, String stsToken) {
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.expiration = expiration;
		this.stsToken = stsToken;
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

	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	public String getStsToken() {
		return stsToken;
	}

	public void setStsToken(String stsToken) {
		this.stsToken = stsToken;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

}
