/*
 * Copyright 2019-2024 workoss (https://www.workoss.com)
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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("ALL")
class ExtensionLoaderUtil {

	private static final ConcurrentMap<Class, ExtensionLoader> LOADER_MAP = new ConcurrentHashMap<>();

	public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> clazz, ExtensionLoaderListener<T> listener) {
		ExtensionLoader<T> loader = LOADER_MAP.get(clazz);
		if (loader == null) {
			synchronized (ExtensionLoaderFactory.class) {
				loader = LOADER_MAP.get(clazz);
				if (loader == null) {
					loader = new ExtensionLoader<>(clazz, listener);
					LOADER_MAP.put(clazz, loader);
				}
			}
		}
		return loader;
	}

}
