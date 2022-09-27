/*
 * Copyright 2019-2022 workoss (https://www.workoss.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * @author workoss
 */
public class ExtensionLoaderFactory {

	private static final Logger log = LoggerFactory.getLogger(ExtensionLoaderFactory.class);

	/**
	 * 获取SPI的扩展
	 * @param clazz 类
	 * @param alias alias
	 * @param listener listener
	 * @param <T> 泛型
	 */
	public static <T> T getSpiExtension(Class<T> clazz, String alias, ExtensionLoaderListener<T> listener) {
		ExtensionLoader<ExtensionFactory> factoryExtensionLoader = getExtensionFactory();
		return getExtension(factoryExtensionLoader.getExtension("spi"), clazz, alias, listener);
	}

	/**
	 * order排序，获取order最小的
	 * @param clazz 类
	 * @param alias alias
	 * @param listener listener
	 * @param <T> 泛型
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
	 * @param clazz 类
	 * @param alias alias
	 * @param listener listener
	 * @param <T> 泛型
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
			}
			catch (Exception e) {
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
