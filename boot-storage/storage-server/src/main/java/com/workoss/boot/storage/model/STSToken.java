package com.workoss.boot.storage.model;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 返回STS token 临时
 *
 * @author workoss
 */
@ToString
@Data
public class STSToken extends BaseStorageModel {

	/**
	 * accessKey accessId
	 */
	private String accessKey;

	/**
	 * secretKey
	 */
	private String secretKey;

	/**
	 * sts token
	 */
	private String stsToken;

	/**
	 * 过期时间
	 */
	private LocalDateTime expiration;

	/**
	 * endpoint
	 */
	private String endpoint;

}
