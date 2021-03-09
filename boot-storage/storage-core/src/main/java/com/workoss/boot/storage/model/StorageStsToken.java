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
