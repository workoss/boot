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
package com.workoss.boot.util.algorithm;

import java.util.Random;

/**
 * @author workoss
 */
@SuppressWarnings("ALL")
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
		int moduleIndex = moduleSuffix;
		int originIndex = moduleSuffix;

		for (int ot = originIndex; originIndex < originString.length() && moduleIndex >= 0;) {
			char oc = originString.charAt(originIndex);
			char mc = moduleString.charAt(moduleIndex);
			if (oc == mc) {
				originIndex--;
				moduleIndex--;
			}
			else {
				// 坏字符规则
				int badMove = badCharacter(moduleString, oc, moduleIndex);
				// 好字符规则
				int goodMove = goodCharacter(moduleString, moduleIndex);
				// 下面两句代码可以这样理解，主串位置不动，模式串向右移动
				originIndex = ot + Math.max(badMove, goodMove);
				moduleIndex = moduleSuffix;
				// ot就是中间变量
				ot = originIndex;
			}
		}

		if (moduleIndex < 0) {
			// 多减了一次
			return originIndex + 1;
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
