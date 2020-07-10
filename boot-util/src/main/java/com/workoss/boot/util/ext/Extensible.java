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
package com.workoss.boot.util.ext;

import java.lang.annotation.*;

/**
 * @author: workoss
 * @date: 2018-11-09 09:39
 * @version:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Extensible {

	/**
	 * 指定自定义扩展文件名称，默认就是全类名
	 * @return 自定义扩展文件名称
	 */
	String file() default "";

	/**
	 * 扩展类是否使用单例，默认使用
	 * @return 是否使用单例
	 */
	boolean singleton() default true;

	/**
	 * 扩展类是否需要编码，默认不需要
	 * @return 是否需要编码
	 */
	boolean coded() default false;

}
