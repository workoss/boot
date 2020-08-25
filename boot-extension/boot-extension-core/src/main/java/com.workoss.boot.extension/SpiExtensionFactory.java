package com.workoss.boot.extension;

/**
 * @author workoss
 */
@Extension(value = "spi", order = 99, override = true)
public class SpiExtensionFactory implements ExtensionFactory {

	@Override
	public <T> T getExtension(Class<T> tClass, String alias) {
		return getExtension(tClass, alias, null);
	}

	@Override
	public <T> T getExtension(Class<T> tClass, String alias, ExtensionLoaderListener<T> listener) {
		ExtensionLoader<T> extensionLoader = ExtensionLoaderUtil.getExtensionLoader(tClass, listener);
		if (extensionLoader == null) {
			throw new ExtensionException("no such class:" + tClass + " extension");
		}
		return extensionLoader.getExtension(alias);
	}

}
