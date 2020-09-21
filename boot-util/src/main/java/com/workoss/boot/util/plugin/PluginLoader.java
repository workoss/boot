package com.workoss.boot.util.plugin;

import com.workoss.boot.util.ArrayUtils;
import com.workoss.boot.util.concurrent.TemporaryClassLoaderContext;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * 插件加载
 *
 * @author workoss
 */
public class PluginLoader {

	private final ClassLoader pluginClassLoader;

	public PluginLoader(ClassLoader pluginClassLoader) {
		this.pluginClassLoader = pluginClassLoader;
	}

	public static ClassLoader createPluginLoader(PluginDescriptor descriptor, ClassLoader parentClassLoader,
			String[] alwaysParentFirstPatterns) {
		return new PluginClassLoader(descriptor.getPluginResourceURLs(), parentClassLoader,
				ArrayUtils.concat(alwaysParentFirstPatterns, descriptor.getLoaderExcludePatterns()));
	}

	public static PluginLoader create(PluginDescriptor descriptor, ClassLoader parentClassLoader,
			String[] alwaysParentFirstPatterns) {
		return new PluginLoader(createPluginLoader(descriptor, parentClassLoader, alwaysParentFirstPatterns));
	}

	public <P> Iterator<P> load(Class<P> service) {
		try (TemporaryClassLoaderContext ignored = TemporaryClassLoaderContext.of(pluginClassLoader)) {
			return new ContextClassLoaderSettingIterator<>(ServiceLoader.load(service, pluginClassLoader).iterator(),
					pluginClassLoader);
		}
		catch (Exception e) {
			throw new PluginRuntimeException(e);
		}
	}

	static class ContextClassLoaderSettingIterator<P> implements Iterator<P> {

		private final Iterator<P> delegate;

		private final ClassLoader pluginClassLoader;

		public ContextClassLoaderSettingIterator(Iterator<P> delegate, ClassLoader pluginClassLoader) {
			this.delegate = delegate;
			this.pluginClassLoader = pluginClassLoader;
		}

		@Override
		public boolean hasNext() {
			return delegate.hasNext();
		}

		@Override
		public P next() {
			try (TemporaryClassLoaderContext ignored = TemporaryClassLoaderContext.of(pluginClassLoader)) {
				return delegate.next();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

	}

	private static final class PluginClassLoader extends URLClassLoader {

		private static final ClassLoader PLATFORM_OR_BOOTSTRAP_LOADER;

		private final ClassLoader pluginClassLoader;

		private final String[] allowedPluginPackages;

		private final String[] allowedResourcePrefixes;

		static {
			ClassLoader platformLoader = null;
			try {
				platformLoader = (ClassLoader) ClassLoader.class.getMethod("getPlatformClassLoader").invoke(null);
			}
			catch (NoSuchMethodException e) {
				// on Java 8 this method does not exist, but using null indicates the
				// bootstrap loader that we want
				// to have
			}
			catch (Exception e) {
				throw new IllegalStateException("Cannot retrieve platform classloader on Java 9+", e);
			}
			PLATFORM_OR_BOOTSTRAP_LOADER = platformLoader;

			ClassLoader.registerAsParallelCapable();
		}

		PluginClassLoader(URL[] pluginResourceURLs, ClassLoader pluginClassLoader, String[] allowedPluginPackages) {
			super(pluginResourceURLs, PLATFORM_OR_BOOTSTRAP_LOADER);
			this.pluginClassLoader = pluginClassLoader;
			this.allowedPluginPackages = allowedPluginPackages;
			allowedResourcePrefixes = Arrays.stream(allowedPluginPackages)
					.map(packageName -> packageName.replace('.', '/')).toArray(String[]::new);
		}

		@Override
		protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
			synchronized (getClassLoadingLock(name)) {
				final Class<?> loadedClass = findLoadedClass(name);
				if (loadedClass != null) {
					return resolveIfNeeded(resolve, loadedClass);
				}
				if (isAllowedPluginClass(name)) {
					try {
						return resolveIfNeeded(resolve, pluginClassLoader.loadClass(name));
					}
					catch (Exception e) {

					}
				}
			}
			return super.loadClass(name, resolve);
		}

		@Override
		public URL getResource(final String name) {
			if (isAllowedPluginResource(name)) {
				return pluginClassLoader.getResource(name);
			}
			return super.getResource(name);
		}

		@Override
		public Enumeration<URL> getResources(final String name) throws IOException {
			if (isAllowedPluginResource(name)) {
				return pluginClassLoader.getResources(name);
			}
			return super.getResources(name);
		}

		private Class<?> resolveIfNeeded(final boolean resolve, final Class<?> loadedClass) {
			if (resolve) {
				resolveClass(loadedClass);
			}
			return loadedClass;
		}

		private boolean isAllowedPluginClass(final String name) {
			return Arrays.stream(allowedPluginPackages).anyMatch(name::startsWith);
		}

		private boolean isAllowedPluginResource(final String name) {
			return Arrays.stream(allowedResourcePrefixes).anyMatch(name::startsWith);
		}

	}

}
