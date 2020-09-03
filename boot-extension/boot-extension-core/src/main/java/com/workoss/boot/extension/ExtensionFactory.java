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

/**
 * 支持SPI spring
 *
 * @author workoss
 */
@Extensible
public interface ExtensionFactory {

	/**
	 * getExtension
	 * @param tClass 类
	 * @param alias 别称 spi key
	 * @param <T> 泛型
	 * @return 实例
	 */
	<T> T getExtension(Class<T> tClass, String alias);

	/**
	 * getExtension
	 * @param tClass 类
	 * @param alias 别称
	 * @param listener 监听
	 * @param <T> 反省
	 * @return 实例
	 */
	<T> T getExtension(Class<T> tClass, String alias, ExtensionLoaderListener<T> listener);

}
