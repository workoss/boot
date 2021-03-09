package com.workoss.boot.storage.config;

import java.util.Map;

/**
 * 混合配置
 *
 * @author workoss
 */
public interface MultiStorageClientConfig {

	String PREFIX = "popeye.storage";

	String ENABLED = "enabled";

	String HEALTH = "health";

	/**
	 * 是否启用
	 * @return true/false
	 */
	boolean isEnabled();

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
