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
package com.workoss.boot.util;

/**
 * @author: workoss
 * @date: 2018-12-13 17:25
 * @version:
 */
public class ExceptionUtils {

	/**
	 * 返回堆栈信息（e.printStackTrace()的内容）
	 * @param e Throwable
	 * @return 异常堆栈信息
	 */
	public static String toString(Throwable e) {
		StackTraceElement[] traces = e.getStackTrace();
		StringBuilder sb = new StringBuilder(1024);
		sb.append(e.toString()).append("\n");
		if (traces != null) {
			for (StackTraceElement trace : traces) {
				sb.append("\tat ").append(trace).append("\n");
			}
		}
		return sb.toString();
	}

	/**
	 * 返回消息+简短堆栈信息（e.printStackTrace()的内容）
	 * @param e Throwable
	 * @param stackLevel 堆栈层级
	 * @return 异常堆栈信息
	 */
	public static String toShortString(Throwable e, int stackLevel) {
		StackTraceElement[] traces = e.getStackTrace();
		StringBuilder sb = new StringBuilder(1024);
		sb.append(e.toString()).append("\t");
		if (traces != null) {
			for (int i = 0; i < traces.length; i++) {
				if (i < stackLevel) {
					sb.append("\tat ").append(traces[i]).append("\t");
				}
				else {
					break;
				}
			}
		}
		return sb.toString();
	}

}
