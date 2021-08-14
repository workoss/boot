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
package com.workoss.boot.storage.web.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * web 签名请求参数
 *
 * @author workoss
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UploadSignParam extends StorageParam {

	@NotBlank(message = "{security.uploadsign.bucketName.notblank}")
	private String bucketName;

	@NotBlank(message = "{security.uploadsign.key.notblank}")
	private String key;

	private String mimeType;

	private String successActionStatus;

}
