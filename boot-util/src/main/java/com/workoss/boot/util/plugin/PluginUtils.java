package com.workoss.boot.util.plugin;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * 插件工具类
 *
 * @author workoss
 */
public final class PluginUtils {

	private PluginUtils() {
		throw new AssertionError("Singleton class.");
	}

	public static PluginManager createPluginManagerFromRootFolder(PluginConfig pluginConfig) {
		if (pluginConfig.getPluginsPath().isPresent()) {
			try {
				Collection<PluginDescriptor> pluginDescriptors = new DirectoryBasedPluginFinder(
						pluginConfig.getPluginsPath().get()).findPlugins();
				return new DefaultPluginManager(pluginDescriptors, pluginConfig.getAlwaysParentFirstPatterns());
			}
			catch (IOException e) {
				throw new PluginRuntimeException("Exception when trying to initialize plugin system.", e);
			}
		}
		else {
			return new DefaultPluginManager(Collections.emptyList(), pluginConfig.getAlwaysParentFirstPatterns());
		}
	}

}
