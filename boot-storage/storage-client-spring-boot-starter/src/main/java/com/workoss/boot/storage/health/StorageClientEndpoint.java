/*
 * Copyright 2019-2023 workoss (https://www.workoss.com)
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
package com.workoss.boot.storage.health;

import com.workoss.boot.storage.config.MultiStorageClientConfig;
import com.workoss.boot.storage.config.StorageClientConfig;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * storage endpoint
 *
 * @author workoss
 */
@SuppressWarnings("unused")
@Endpoint(id = "storage")
public class StorageClientEndpoint {

	private final MultiStorageClientConfig multiStorageClientConfig;

	public StorageClientEndpoint(MultiStorageClientConfig multiStorageClientConfig) {
		this.multiStorageClientConfig = multiStorageClientConfig;
	}

	@ReadOperation
	public Map<String, Object> storageData() {
		Map<String, StorageClientConfig> clientConfigMap = multiStorageClientConfig.getClientConfigs();
		if (clientConfigMap == null) {
			clientConfigMap = new HashMap<>();
		}
		if (multiStorageClientConfig.getDefaultClient() != null) {
			clientConfigMap.put((multiStorageClientConfig.getDefaultClientKey() != null
					? multiStorageClientConfig.getDefaultClientKey() : MultiStorageClientConfig.DEFAULT_CLIENT_KEY),
					multiStorageClientConfig.getDefaultClient());
		}
		return clientConfigMap.entrySet().stream().peek(entry -> {
			StorageClientConfig clientConfig = entry.getValue();
			clientConfig.setAccessKey(null);
			clientConfig.setSecretKey(null);
			entry.setValue(clientConfig);
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

}
