package com.workoss.boot.storage.web.vo;

import com.workoss.boot.storage.model.ThirdPlatformType;
import lombok.Data;

/**
 * 签名返回
 *
 * @author workoss
 */
@Data
public class UploadSignVO {

	private ThirdPlatformType storageType;

	/**
	 * 临时accessKey
	 */
	private String accessKey;

	/**
	 * 临时token
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
