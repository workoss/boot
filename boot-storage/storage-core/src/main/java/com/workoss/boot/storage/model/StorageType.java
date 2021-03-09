package com.workoss.boot.storage.model;

/**
 * 存储类型
 *
 * @author workoss
 */
public enum StorageType {

	/**
	 * aws
	 */
	S3,
	/**
	 * 阿里云OSS
	 */
	OSS,
	/**
	 * 腾讯云COS
	 */
	COS,
	/**
	 * 华为云OBS
	 */
	OBS,
	/**
	 * 七牛对象存储
	 */
	QINIU,

}
