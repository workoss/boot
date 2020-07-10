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
package com.workoss.boot.util.algorithm;

import java.util.Random;

public class BMUtil {

	public static int badCharacter(String moduleString, char badChar, int badCharSuffix) {
		return badCharSuffix - moduleString.lastIndexOf(badChar, badCharSuffix);
	}

	public static int goodCharacter(String moduleString, int goodCharSuffix) {
		int result = -1;
		// 模式串长度
		int moduleLength = moduleString.length();
		// 好字符数
		int goodCharNum = moduleLength - 1 - goodCharSuffix;

		for (; goodCharNum > 0; goodCharNum--) {
			String endSection = moduleString.substring(moduleLength - goodCharNum, moduleLength);
			String startSection = moduleString.substring(0, goodCharNum);
			if (startSection.equals(endSection)) {
				result = moduleLength - goodCharNum;
			}
		}

		return result;
	}

	public static int match(String originString, String moduleString) {
		// 主串
		if (originString == null || originString.length() <= 0) {
			return -1;
		}
		// 模式串
		if (moduleString == null || moduleString.length() <= 0) {
			return -1;
		}
		// 如果模式串的长度大于主串的长度，那么一定不匹配
		if (originString.length() < moduleString.length()) {
			return -1;
		}

		int moduleSuffix = moduleString.length() - 1;
		int module_index = moduleSuffix;
		int origin_index = moduleSuffix;

		for (int ot = origin_index; origin_index < originString.length() && module_index >= 0;) {
			char oc = originString.charAt(origin_index);
			char mc = moduleString.charAt(module_index);
			if (oc == mc) {
				origin_index--;
				module_index--;
			}
			else {
				// 坏字符规则
				int badMove = badCharacter(moduleString, oc, module_index);
				// 好字符规则
				int goodMove = goodCharacter(moduleString, module_index);
				// 下面两句代码可以这样理解，主串位置不动，模式串向右移动
				origin_index = ot + Math.max(badMove, goodMove);
				module_index = moduleSuffix;
				// ot就是中间变量
				ot = origin_index;
			}
		}

		if (module_index < 0) {
			// 多减了一次
			return origin_index + 1;
		}

		return -1;
	}

	public static String generateString(int length) {
		String baseString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

		StringBuilder result = new StringBuilder();

		Random random = new Random();
		for (int i = 0; i < length; i++) {
			result.append(baseString.charAt(random.nextInt(baseString.length())));
		}

		return result.toString();
	}

}
