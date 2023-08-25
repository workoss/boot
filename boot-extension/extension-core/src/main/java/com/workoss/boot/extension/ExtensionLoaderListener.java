/*
 * Copyright 2019-2023 workoss (https://www.workoss.com)
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
 * extension listener
 *
 * @author workoss
 */
@FunctionalInterface
public interface ExtensionLoaderListener<T> {

	/**
	 * 当扩展点加载时，触发的事件
	 * @param extensionClass 扩展点类对象
	 */
	void onLoad(ExtensionClass<T> extensionClass);

}
