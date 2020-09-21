package com.workoss.boot.util.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Stores the configuration for plugins mechanism.
 *
 * @author workoss
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class PluginConfig {

	private static final Logger LOG = LoggerFactory.getLogger(PluginConfig.class);

	private final Optional<Path> pluginsPath;

	private final String[] alwaysParentFirstPatterns;

	public PluginConfig(Optional<Path> pluginsPath, String[] alwaysParentFirstPatterns) {
		this.pluginsPath = pluginsPath;
		this.alwaysParentFirstPatterns = alwaysParentFirstPatterns;
	}

	public Optional<Path> getPluginsPath() {
		return pluginsPath;
	}

	public String[] getAlwaysParentFirstPatterns() {
		return alwaysParentFirstPatterns;
	}

	public static Optional<File> getPluginsDir() {
		String pluginsDir = System.getenv().getOrDefault("PLUGINS_DIR", "plugins");
		File pluginsDirFile = new File(pluginsDir);
		if (!pluginsDirFile.isDirectory()) {
			LOG.warn("The plugins directory [{}] does not exist.", pluginsDirFile);
			return Optional.empty();
		}
		return Optional.of(pluginsDirFile);
	}

}
