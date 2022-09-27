/*
 * Copyright 2019-2022 workoss (https://www.workoss.com)
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
package com.workoss.boot.storage.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 签名返回
 *
 * @author workoss
 */
@EqualsAndHashCode(callSuper = true)
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
