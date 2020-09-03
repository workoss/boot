/*
 * Copyright © 2020-2021 workoss (workoss@icloud.com)
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

/**
 * spiExtensionFactory
 *
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
