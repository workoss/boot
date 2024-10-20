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
package com.workoss.boot.util.reflect;

/**
 * @author workoss
 */
public class ClassLoaderUtils {

	/**
	 * 得到当前ClassLoader，先找线程池的，找不到就找中间件所在的ClassLoader
	 * @return ClassLoader
	 */
	public static ClassLoader getCurrentClassLoader() {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl == null) {
			cl = ClassLoaderUtils.class.getClassLoader();
		}
		return cl == null ? ClassLoader.getSystemClassLoader() : cl;
	}

	/**
	 * 得到当前ClassLoader
	 * @param clazz 某个类
	 * @return ClassLoader
	 */
	public static ClassLoader getClassLoader(Class<?> clazz) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader != null) {
			return loader;
		}
		if (clazz != null) {
			loader = clazz.getClassLoader();
			if (loader != null) {
				return loader;
			}
		}
		return ClassLoader.getSystemClassLoader();
	}

}
