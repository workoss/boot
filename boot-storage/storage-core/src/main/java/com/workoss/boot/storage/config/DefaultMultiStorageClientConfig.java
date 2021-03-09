package com.workoss.boot.storage.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认多客户端配置
 *
 * @author workoss
 */
public class DefaultMultiStorageClientConfig implements MultiStorageClientConfig {

	private boolean enabled = true;

	private boolean health = true;

	private String defaultClientKey = "default";

	/**
	 * 单个客户端配置
	 */
	private StorageClientConfig defaultClient = new StorageClientConfig();

	/**
	 * 多个客户端配置
	 */
	private Map<String, StorageClientConfig> clientConfigs = new HashMap<>();

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isHealth() {
		return health;
	}

	public void setHealth(boolean health) {
		this.health = health;
	}

	@Override
	public String getDefaultClientKey() {
		return defaultClientKey;
	}

	public void setDefaultClientKey(String defaultClientKey) {
		this.defaultClientKey = defaultClientKey;
	}

	@Override
	public StorageClientConfig getDefaultClient() {
		return defaultClient;
	}

	public void setDefaultClient(StorageClientConfig defaultClient) {
		this.defaultClient = defaultClient;
	}

	@Override
	public Map<String, StorageClientConfig> getClientConfigs() {
		return clientConfigs;
	}

	public void setClientConfigs(Map<String, StorageClientConfig> clientConfigs) {
		this.clientConfigs = clientConfigs;
	}

}
