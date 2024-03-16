/*
 * Copyright 2019-2024 workoss (https://www.workoss.com)
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

import java.time.LocalDateTime;

/**
 * 账号对象
 *
 * @author workoss
 */
@Data
public class StorageAccount {

	/**
	 * 账号类别
	 */
	private ThirdPlatformType accountType;

	/**
	 * 三方账号ak
	 */
	private String accessKey;

	/**
	 * 账号配置
	 */
	private String config;

	/**
	 * 授权策略模板
	 */
	private String policyTemplate;

	/**
	 * 状态 ON,OFF
	 */
	private AccountState state;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;

	/**
	 * 修改时间
	 */
	private LocalDateTime modifyTime;

}
