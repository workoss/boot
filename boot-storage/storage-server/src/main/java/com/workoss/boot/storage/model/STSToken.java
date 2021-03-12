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
package com.workoss.boot.storage.model;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 返回STS token 临时
 *
 * @author workoss
 */
@ToString
@Data
public class STSToken extends BaseStorageModel {

	/**
	 * accessKey accessId
	 */
	private String accessKey;

	/**
	 * secretKey
	 */
	private String secretKey;

	/**
	 * sts token
	 */
	private String stsToken;

	/**
	 * 过期时间
	 */
	private LocalDateTime expiration;

	/**
	 * endpoint
	 */
	private String endpoint;

}
