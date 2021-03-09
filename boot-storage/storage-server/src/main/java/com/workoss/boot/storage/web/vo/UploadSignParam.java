package com.workoss.boot.storage.web.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UploadSignParam extends StorageParam {

	@NotBlank(message = "{security.uploadsign.bucketName.notblank}")
	private String bucketName;

	@NotBlank(message = "{security.uploadsign.key.notblank}")
	private String key;

	private String mimeType;

	private String successActionStatus;

}
