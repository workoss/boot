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
import com.workoss.boot.storage.config.MultiStorageClientConfig;
import com.workoss.boot.storage.client.StorageClient;
import com.workoss.boot.storage.config.StorageClientConfig;
import com.workoss.boot.storage.exception.StorageClientNotFoundException;
import com.workoss.boot.storage.exception.StorageException;
import com.workoss.boot.storage.model.StorageType;
import com.workoss.boot.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * storage template
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
abstract class BaseStorageTemplate implements StorageTemplate {

	private static final Logger log = LoggerFactory.getLogger(BaseStorageTemplate.class);

	private final ConcurrentHashMap<String, StorageClient> clientMap = new ConcurrentHashMap<>();

	private MultiStorageClientConfig multiStorageClientConfig;

	public void setMultiStorageClientConfig(MultiStorageClientConfig multiStorageClientConfig) {
		this.multiStorageClientConfig = multiStorageClientConfig;
	}

	public StorageClient client() {
		String defaultName = "default";
		if (multiStorageClientConfig != null
				&& StringUtils.isNotBlank(multiStorageClientConfig.getDefaultClientKey())) {
			defaultName = multiStorageClientConfig.getDefaultClientKey();
		}
		return client(defaultName);
	}

	public StorageClient client(@NonNull String key) {
		StorageClient storageClient = clientMap.get(key);
		if (storageClient == null) {
			throw new StorageClientNotFoundException("client:" + key + " not found");
		}
		return storageClient;
	}

	public Map<String, StorageClient> allClients() {
		return clientMap;
	}

	public void destroy() throws Exception {
		clientMap.entrySet().stream().forEach(clientEntry -> {
			try {
				clientEntry.getValue().shutdown();
				log.info("【storage】Storage key:{} close success", clientEntry.getKey());
			}
			catch (Exception e) {
				log.error("【storage】Storage key:{} close error", clientEntry.getKey(), e);
			}
		});
	}

	public void afterPropertiesSet() throws Exception {
		Map<StorageType, StorageClient> storageClientMap = loadStorageClient();
		Map<StorageType, Integer> initNumMap = new HashMap<StorageType, Integer>(4);
		if (multiStorageClientConfig != null && multiStorageClientConfig.isEnabled()) {
			log.info("【storage】multiStorageClientConfig init start");
			Optional.ofNullable(multiStorageClientConfig.getClientConfigs()).orElse(new HashMap<>(16)).entrySet()
					.stream()
					.filter(storageClientConfigEntry -> storageClientMap
							.containsKey(storageClientConfigEntry.getValue().getStorageType()))
					.forEach(storageClientConfigEntry -> {
						Integer num = initNumMap.get(storageClientConfigEntry.getValue().getStorageType());
						if (num == null) {
							addStorageClient(storageClientMap.get(storageClientConfigEntry.getValue().getStorageType()),
									storageClientConfigEntry.getKey(), storageClientConfigEntry.getValue());
						}
						else {
							addStorageClient(
									loadStorageClient().get(storageClientConfigEntry.getValue().getStorageType()),
									storageClientConfigEntry.getKey(), storageClientConfigEntry.getValue());
						}
						initNumMap.put(storageClientConfigEntry.getValue().getStorageType(), num == null ? 1 : num + 1);
					});
		}
		StorageClientConfig storageClientConfig = multiStorageClientConfig.getDefaultClient();
		if (storageClientConfig != null) {
			Map<StorageType, StorageClient> defaultClientMap = storageClientMap;
			if (initNumMap.containsKey(storageClientConfig.getStorageType())) {
				defaultClientMap = loadStorageClient();
			}
			addStorageClient(defaultClientMap.get(storageClientConfig.getStorageType()), "default",
					storageClientConfig);
		}
		initNumMap.clear();
	}

	void validStorageClientConfig(StorageClientConfig config) {
		// 校验必填参数 storageType bucketName
		if (StringUtils.isBlank(config.getBucketName()) || config.getStorageType() == null) {
			throw new StorageException("-1", "storageType,bucketName不能为空");
		}
		boolean useStsToken = StringUtils.isNotBlank(config.getTokenUrl());
		if (useStsToken) {
			return;
		}
		if (StringUtils.isBlank(config.getAccessKey()) || StringUtils.isBlank(config.getSecretKey())
				|| StringUtils.isBlank(config.getEndpoint())) {
			throw new StorageException("-1", "非临时授权 accessKey,secretKey,endpoint不能为空");
		}
	}

	void addStorageClient(StorageClient storageClient, String key, StorageClientConfig config) {
		if (StringUtils.isBlank(config.getBucketName())) {
			return;
		}
		if (storageClient == null) {
			throw new StorageException("-1", "没有发现可用的client");
		}
		validStorageClientConfig(config);
		try {
			storageClient.init(config);
			clientMap.put(key.toLowerCase(), storageClient);
			log.info("【storage】StorageClient add client :{} to cache", key.toLowerCase());
		}
		catch (Exception e) {
			log.error("【storage】StorageClient add client to cache {} error", key.toLowerCase(), e);
		}
	}

	Map<StorageType, StorageClient> loadStorageClient() {
		ServiceLoader<StorageClient> serviceLoader = ServiceLoader.load(StorageClient.class,
				this.getClass().getClassLoader());
		Iterator<StorageClient> storageClientIterator = serviceLoader.iterator();
		Map<StorageType, StorageClient> storageClientMap = new HashMap<>(16);
		while (storageClientIterator.hasNext()) {
			StorageClient storageClient = storageClientIterator.next();
			storageClientMap.put(storageClient.type(), storageClient);
		}
		return storageClientMap;
	}

}
