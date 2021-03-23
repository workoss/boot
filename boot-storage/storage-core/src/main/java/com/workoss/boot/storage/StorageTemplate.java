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
package com.workoss.boot.storage;

import com.workoss.boot.annotation.lang.NonNull;
import com.workoss.boot.storage.client.StorageClient;
import com.workoss.boot.storage.config.MultiStorageClientConfig;

import java.util.Map;

/**
 * 对象存储模板
 *
 * @author workoss
 */
public interface StorageTemplate {

	/**
	 * 设置 配置
	 * @param multiStorageClientConfig 配置
	 */
	void setMultiStorageClientConfig(MultiStorageClientConfig multiStorageClientConfig);

	/**
	 * 获取默认客户端
	 * @return 客户端
	 */
	StorageClient client();

	/**
	 * 获取 key 客户端
	 * @param key 关键字
	 * @return 客户端
	 */
	StorageClient client(@NonNull String key);

	/**
	 * 所有客户端
	 * @return 所有客户端
	 */
	Map<String, StorageClient> allClients();

	/**
	 * 销毁
	 * @throws Exception 异常
	 */
	void destroy() throws Exception;

	/**
	 * 初始化执行
	 * @throws Exception 异常
	 */
	void afterPropertiesSet() throws Exception;

}
