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
@Endpoint(id = "storage-client")
public class StorageClientEndpoint {

	private MultiStorageClientConfig multiStorageClientConfig;

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
			clientConfigMap.put(
					(multiStorageClientConfig.getDefaultClientKey() != null ?
							multiStorageClientConfig.getDefaultClientKey() :
							MultiStorageClientConfig.DEFAULT_CLIENT_KEY),
					multiStorageClientConfig.getDefaultClient());
		}
		return clientConfigMap.entrySet()
				.stream()
				.map(entry -> {
					StorageClientConfig clientConfig = new StorageClientConfig();
					clientConfig.setAccessKey(null);
					clientConfig.setSecretKey(null);
					entry.setValue(clientConfig);
					return entry;
				})
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

}
