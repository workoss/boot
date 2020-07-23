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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: 字符串工具类
 * @Author: workoss
 * @Date: 2017/8/11 8:10
 * @Version: 1.0.0
 */
@SuppressWarnings("ALL")
public class StringUtils {

	public static String EMPTY = "";

	public static final String[] EMPTY_STRING_ARRAY = new String[0];

	public static boolean isEmpty(CharSequence cs) {
		return (cs == null) || (cs.length() == 0);
	}

	public static boolean isNotEmpty(CharSequence cs) {
		return !isEmpty(cs);
	}

	public static boolean isNotNull(String str) {
		return isNotEmpty(str);
	}

	public static boolean isNotBlank(CharSequence cs) {
		return !isBlank(cs);
	}

	public static boolean isBlank(CharSequence cs) {
		int strLen;
		if ((cs == null) || ((strLen = cs.length()) == 0)) {
			return true;
		}

		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(cs.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isBlank(CharSequence... cs) {
		for (CharSequence c : cs) {
			if (isBlank(c)) {
				continue;
			}
			else {
				return false;
			}
		}
		return true;
	}

	public static String concat(String... strings) {
		if (strings == null) {
			return null;
		}
		StringJoiner stringJoiner = new StringJoiner("");
		for (String string : strings) {
			stringJoiner.add(string);
		}
		return stringJoiner.toString();
	}

	public static String concat(List<String> list, String delimiter) {
		if (list == null) {
			return null;
		}
		StringJoiner stringJoiner = new StringJoiner(delimiter);
		for (String string : list) {
			stringJoiner.add(string);
		}
		return stringJoiner.toString();
	}

	public static boolean hasLength(CharSequence str) {
		return str != null && str.length() > 0;
	}

	public static boolean compareLength(CharSequence str, int minLength, int maxLength) {
		int strlength = (isBlank(str) ? 0 : str.length());
		if (strlength >= minLength && strlength <= maxLength) {
			return true;
		}
		return false;
	}

	public static boolean hasLength(String str) {
		return hasLength((CharSequence) str);
	}

	public static boolean hasText(CharSequence str) {
		if (!hasLength(str)) {
			return false;
		}
		else {
			int strLen = str.length();

			for (int i = 0; i < strLen; ++i) {
				if (!Character.isWhitespace(str.charAt(i))) {
					return true;
				}
			}

			return false;
		}
	}

	public static boolean isTrue(Object obj) {
		return "true".equals(String.valueOf(obj));
	}

	public static String uncapitalize(String str) {
		int strLen;
		if (str != null && (strLen = str.length()) != 0) {
			char firstChar = str.charAt(0);
			char newChar = Character.toLowerCase(firstChar);
			if (firstChar == newChar) {
				return str;
			}
			else {
				char[] newChars = new char[strLen];
				newChars[0] = newChar;
				str.getChars(1, strLen, newChars, 1);
				return String.valueOf(newChars);
			}
		}
		else {
			return str;
		}
	}

	public static String capitalize(String str) {
		int strLen;
		if (str != null && (strLen = str.length()) != 0) {
			char firstChar = str.charAt(0);
			char newChar = Character.toUpperCase(firstChar);
			if (firstChar == newChar) {
				return str;
			}
			else {
				char[] newChars = new char[strLen];
				newChars[0] = newChar;
				str.getChars(1, strLen, newChars, 1);
				return String.valueOf(newChars);
			}
		}
		else {
			return str;
		}
	}

	/**
	 * 转换为下划线
	 * @param camelCaseName
	 * @return
	 */
	public static String underscoreName(String camelCaseName) {
		StringBuilder result = new StringBuilder();
		if (camelCaseName != null && camelCaseName.length() > 0) {
			result.append(camelCaseName.substring(0, 1).toLowerCase());
			for (int i = 1; i < camelCaseName.length(); i++) {
				char ch = camelCaseName.charAt(i);
				if (Character.isUpperCase(ch)) {
					result.append("_");
					result.append(Character.toLowerCase(ch));
				}
				else {
					result.append(ch);
				}
			}
		}
		return result.toString();
	}

	/**
	 * 转换为驼峰
	 * @param underscoreName
	 * @return
	 */
	public static String camelCaseName(String underscoreName) {
		StringBuilder result = new StringBuilder();
		if (underscoreName != null && underscoreName.length() > 0) {
			boolean flag = false;
			for (int i = 0; i < underscoreName.length(); i++) {
				char ch = underscoreName.charAt(i);
				if ("_".charAt(0) == ch) {
					flag = true;
				}
				else {
					if (flag) {
						result.append(Character.toUpperCase(ch));
						flag = false;
					}
					else {
						result.append(ch);
					}
				}
			}
		}
		return result.toString();

	}

	public static String renderString(String content, Map<String, String> map) {
		return renderString(content, map, null);
	}

	public static String renderString(String content, Map<String, String> map, String nullValue) {
		return renderString(content, "\\$\\{", "\\}", map, nullValue);
	}

	private static ConcurrentHashMap<String, Pattern> PATTERN_MAP = new ConcurrentHashMap<>();

	public static String renderString(String content, String prefixPattern, String suffixPattern,
			Map<String, String> map, String nullValue) {
		if (StringUtils.isBlank(content)) {
			return content;
		}
		if (map == null) {
			map = new HashMap<>();
		}
		try {
			String patternKey = prefixPattern + "-" + suffixPattern;
			Pattern pattern = PATTERN_MAP.get(patternKey);
			if (pattern == null) {
				pattern = Pattern.compile(prefixPattern + "(.+?)" + suffixPattern);
				PATTERN_MAP.put(patternKey, pattern);
			}
			StringBuffer stringBuffer = new StringBuffer();
			Matcher matcher = pattern.matcher(content);
			while (matcher.find()) {
				String name = matcher.group(1);
				String value = map.get(name);
				if (value == null && nullValue == null) {
					continue;
				}
				matcher.appendReplacement(stringBuffer, value == null ? nullValue : value);

			}
			matcher.appendTail(stringBuffer);
			return stringBuffer.toString();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String nameMask(String name) {
		StringBuilder maskBuilder = new StringBuilder(name.length());
		if (name.length() > 1) {
			for (int i = 1, j = name.length(); i < j; i++) {
				maskBuilder.append("*");
			}
		}
		return name.replaceAll("(\\D{1})(.*)", "$1" + maskBuilder.toString());
	}

	public static String phoneMask(String phone) {
		return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
	}

}
