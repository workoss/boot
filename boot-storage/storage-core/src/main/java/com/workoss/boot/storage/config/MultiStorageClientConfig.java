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
package com.workoss.boot.storage.config;

import java.util.Map;

/**
 * 混合配置
 *
 * @author workoss
 */
public interface MultiStorageClientConfig {

	String PREFIX = "boot.storage";

	String ENABLED = "enabled";

	String HEALTH = "health";

	/**
	 * 是否启用
	 * @return true/false
	 */
	boolean isEnabled();

	/**
	 * 是否健康监测
	 * @return true/false
	 */
	boolean isHealth();

	/**
	 * 多客户端 默认key名称
	 * @return 客户端名称
	 */
	String getDefaultClientKey();

	/**
	 * 单客户端
	 * @return 客户端配置
	 */
	StorageClientConfig getDefaultClient();

	/**
	 * 多客户端配置
	 * @return 多客户端配置
	 */
	Map<String, StorageClientConfig> getClientConfigs();

}
