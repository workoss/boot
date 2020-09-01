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

import java.lang.annotation.*;

/**
 * 扩展
 *
 * @author: workoss
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Extension {

	/**
	 * 扩展点名字
	 * @return 扩展点名字
	 */
	String value();

	/**
	 * 扩展点编码，默认不需要，当接口需要编码的时候需要
	 * @return 扩展点编码
	 * @see Extensible#coded()
	 */
	byte code() default -1;

	/**
	 * 优先级排序，默认不需要，大的优先级高
	 * @return 排序
	 */
	int order() default 0;

	/**
	 * 是否覆盖其它低{@link #order()}的同名扩展
	 * @return 是否覆盖其它低排序的同名扩展
	 * @since 5.2.0
	 */
	boolean override() default false;

	/**
	 * 排斥其它扩展，可以排斥掉其它低{@link #order()}的扩展
	 * @return 排斥其它扩展
	 * @since 5.2.0
	 */
	String[] rejection() default {};

}
