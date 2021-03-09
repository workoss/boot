package com.workoss.boot.storage.web.vo;

import com.workoss.boot.storage.model.ThirdPlatformType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 公共参数，用于查询到db中ak/sk配置信息
 *
 * @author workoss
 */
@Data
public class StorageParam {

	private String tenentId = "default";

	@NotNull(message = "{security.storage.notnull}")
	private ThirdPlatformType storageType;

}
