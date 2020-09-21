package com.workoss.boot.util.plugin;

import java.util.*;
import java.util.stream.StreamSupport;

/**
 * PluginManager 默认实现
 *
 * @author workoss
 */
public class DefaultPluginManager implements PluginManager {

	/**
	 * Parent-classloader to all classloader that are used for plugin loading. We expect
	 * that this is thread-safe.
	 */
	private final ClassLoader parentClassLoader;

	/** A collection of descriptions of all plugins known to this plugin manager. */
	private final Collection<PluginDescriptor> pluginDescriptors;

	/**
	 * List of patterns for classes that should always be resolved from the parent
	 * ClassLoader.
	 */
	private final String[] alwaysParentFirstPatterns;

	DefaultPluginManager() {
		this.parentClassLoader = null;
		this.pluginDescriptors = null;
		this.alwaysParentFirstPatterns = null;
	}

	public DefaultPluginManager(Collection<PluginDescriptor> pluginDescriptors, String[] alwaysParentFirstPatterns) {
		this(pluginDescriptors, DefaultPluginManager.class.getClassLoader(), alwaysParentFirstPatterns);
	}

	public DefaultPluginManager(Collection<PluginDescriptor> pluginDescriptors, ClassLoader parentClassLoader,
			String[] alwaysParentFirstPatterns) {
		this.parentClassLoader = parentClassLoader;
		this.pluginDescriptors = pluginDescriptors;
		this.alwaysParentFirstPatterns = alwaysParentFirstPatterns;
	}

	@Override
	public <P> Iterator<P> load(Class<P> service) {
		return pluginDescriptors.stream().flatMap(descriptor -> {
			PluginLoader pluginLoader = PluginLoader.create(descriptor, parentClassLoader, alwaysParentFirstPatterns);
			Iterator<P> iterator = pluginLoader.load(service);
			return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
		}).iterator();
	}

	@Override
	public String toString() {
		return "PluginManager{" + "parentClassLoader=" + parentClassLoader + ", pluginDescriptors=" + pluginDescriptors
				+ ", alwaysParentFirstPatterns=" + Arrays.toString(alwaysParentFirstPatterns) + '}';
	}

}
