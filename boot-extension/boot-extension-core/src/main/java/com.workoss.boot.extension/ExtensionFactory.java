package com.workoss.boot.extension;

/**
 * 支持SPI spring
 * @author workoss
 */
@Extensible
public interface ExtensionFactory {
	/**
	 * getExtension
	 * @param tClass
	 * @param alias
	 * @param <T>
	 * @return
	 */
	<T> T getExtension(Class<T> tClass, String alias);

	/**
	 * getExtension
	 * @param tClass
	 * @param alias
	 * @param listener
	 * @param <T>
	 * @return
	 */
	<T> T getExtension(Class<T> tClass, String alias, ExtensionLoaderListener<T> listener);

}
