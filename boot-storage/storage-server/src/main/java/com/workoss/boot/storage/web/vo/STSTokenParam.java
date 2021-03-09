package com.workoss.boot.storage.web.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 生成stsToken参数
 */
@Data
public class STSTokenParam extends StorageParam {

	private String bucketName;

	/**
	 * 动作
	 */
	private String action;

	/**
	 * 文件key
	 */
	@NotBlank(message = "{security.ststoken.key.notblank}")
	private String key;

}
