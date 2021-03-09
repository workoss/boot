package com.workoss.boot.storage.model;

import lombok.Data;

/**
 * 签名返回
 *
 * @author workoss
 */
@Data
public class UploadSign extends BaseStorageModel {

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

}
