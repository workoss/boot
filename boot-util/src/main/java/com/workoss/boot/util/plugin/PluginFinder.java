package com.workoss.boot.util.plugin;

import java.io.IOException;
import java.util.Collection;

/**
 * Implementations of this interface provide mechanisms to locate plugins and create
 * corresponding {@link PluginDescriptor} objects. The result can then be used to
 * initialize a {@link PluginLoader}.
 *
 * @author workoss
 */
public interface PluginFinder {
	/**
	 * 查询插件
	 * @return 集合
	 * @throws IOException io异常
	 */
	Collection<PluginDescriptor> findPlugins() throws IOException;

}
