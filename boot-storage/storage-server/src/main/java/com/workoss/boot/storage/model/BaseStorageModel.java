package com.workoss.boot.storage.model;

import lombok.Data;
import lombok.ToString;

/**
 * 公共参数
 *
 * @author workoss
 */
@ToString
@Data
public class BaseStorageModel {

	private String tenentId;

	private ThirdPlatformType storageType;

	private String bucketName;

	private String domain;

}
