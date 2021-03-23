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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 生成stsToken参数
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
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
