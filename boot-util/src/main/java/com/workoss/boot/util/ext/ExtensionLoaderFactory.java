package com.workoss.boot.util.ext;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: workoss
 * @date: 2018-12-13 17:15
 * @version:
 */
public class ExtensionLoaderFactory {
    private ExtensionLoaderFactory() {
    }

    /**
     * All extension loader {Class : ExtensionLoader}
     */
    private static final ConcurrentMap<Class, ExtensionLoader> LOADER_MAP = new ConcurrentHashMap<Class, ExtensionLoader>();

    /**
     * Get extension loader by extensible class with listener
     *
     * @param clazz    Extensible class
     * @param listener Listener of ExtensionLoader
     * @param <T>      Class
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

    /**
     * Get extension loader by extensible class without listener
     *
     * @param clazz Extensible class
     * @param <T>   Class
     * @return ExtensionLoader of this class
     */
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> clazz) {
        return getExtensionLoader(clazz, null);
    }
}
