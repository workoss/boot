/*
 * The MIT License
 * Copyright © 2020-2021 workoss
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.workoss.boot.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ExtensionLoaderFactory
 *
 * @author: workoss
 */
public class ExtensionLoaderFactory {

	private static final Logger log = LoggerFactory.getLogger(ExtensionLoaderFactory.class);

	/**
	 * 获取SPI的扩展
	 *
	 * @param clazz    类
	 * @param alias    alias
	 * @param listener listener
	 * @param <T>      泛型
	 */
	public static <T> T getSpiExtension(Class<T> clazz, String alias, ExtensionLoaderListener<T> listener) {
		ExtensionLoader<ExtensionFactory> factoryExtensionLoader = getExtensionFactory();
		return getExtension(factoryExtensionLoader.getExtension("spi"), clazz, alias, listener);
	}

	/**
	 * order排序，获取order最小的
	 *
	 * @param clazz    类
	 * @param alias    alias
	 * @param listener listener
	 * @param <T>      泛型
	 * @return 实例
	 */
	public static <T> T getFirstExtension(Class<T> clazz, String alias, ExtensionLoaderListener<T> listener) {
		ExtensionLoader<ExtensionFactory> factoryExtensionLoader = getExtensionFactory();
		ExtensionClass<ExtensionFactory> extensionClass = factoryExtensionLoader.getAllExtensions().entrySet().stream()
				.sorted(Comparator.comparingInt(t -> t.getValue().getOrder())).findFirst().get().getValue();
		return extensionClass.getExtInstance().getExtension(clazz, alias, listener);
	}

	/**
	 * 直到获取到为止 按照顺序
	 *
	 * @param clazz    类
	 * @param alias    alias
	 * @param listener listener
	 * @param <T>      泛型
	 * @return 实例
	 */
	public static <T> T getMixExtension(Class<T> clazz, String alias, ExtensionLoaderListener<T> listener) {
		ExtensionLoader<ExtensionFactory> factoryExtensionLoader = getExtensionFactory();
		List<ExtensionClass<ExtensionFactory>> extensionClassList = factoryExtensionLoader.getAllExtensions().entrySet()
				.stream().sorted(Comparator.comparingInt(t -> t.getValue().getOrder())).map(Map.Entry::getValue)
				.collect(Collectors.toList());
		for (ExtensionClass<ExtensionFactory> extensionFactoryExtensionClass : extensionClassList) {
			try {
				T t = getExtension(extensionFactoryExtensionClass.getExtInstance(), clazz, alias, listener);
				if (t != null) {
					return t;
				}
			} catch (Exception e) {
				log.debug("extension mix :{}", e.getMessage());
			}
			continue;
		}
		throw new ExtensionException("no such class:" + alias + " extension");
	}

	public static <T> T getExtension(String factoryAlias, Class<T> clazz, String alias,
									 ExtensionLoaderListener<T> listener) {
		ExtensionLoader<ExtensionFactory> factoryExtensionLoader = getExtensionFactory();
		return getExtension(factoryExtensionLoader.getExtension(factoryAlias), clazz, alias, listener);
	}

	protected static <T> T getExtension(ExtensionFactory extensionFactory, Class<T> clazz, String alias,
										ExtensionLoaderListener<T> listener) {
		if (ExtensionFactory.class == clazz) {
			return (T) extensionFactory;
		}
		return extensionFactory.getExtension(clazz, alias, listener);
	}

	protected static ExtensionLoader<ExtensionFactory> getExtensionFactory() {
		ExtensionLoader<ExtensionFactory> extensionLoader = ExtensionLoaderUtil
				.getExtensionLoader(ExtensionFactory.class, null);
		if (extensionLoader == null) {
			throw new ExtensionException("no such class:ExtensionFactory extension");
		}
		return extensionLoader;
	}

}
