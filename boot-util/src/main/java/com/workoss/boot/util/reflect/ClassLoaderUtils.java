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
package com.workoss.boot.util.reflect;

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
