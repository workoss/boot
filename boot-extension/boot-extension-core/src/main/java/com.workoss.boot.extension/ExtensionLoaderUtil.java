package com.workoss.boot.extension;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class ExtensionLoaderUtil {

	private static final ConcurrentMap<Class, ExtensionLoader> LOADER_MAP = new ConcurrentHashMap<Class, ExtensionLoader>();

	/**
	 * Get extension loader by extensible class with listener
	 * @param clazz Extensible class
	 * @param listener Listener of ExtensionLoader
	 * @param <T> Class
	 * @return ExtensionLoader of this class
	 */
	public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> clazz, ExtensionLoaderListener<T> listener) {
		ExtensionLoader<T> loader = LOADER_MAP.get(clazz);
		if (loader == null) {
			synchronized (ExtensionLoaderFactory.class) {
				loader = LOADER_MAP.get(clazz);
				if (loader == null) {
					loader = new ExtensionLoader<T>(clazz, listener);
					LOADER_MAP.put(clazz, loader);
				}
			}
		}
		return loader;
	}

}
