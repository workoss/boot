/*
 * Copyright © 2020-2021 workoss (workoss@icloud.com)
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
package com.workoss.boot.util.text;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author workoss
 */
@SuppressWarnings("ALL")
public class BaseEncodeUtil {

	private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };

	/**
	 * Hex编码, 将byte[]编码为String，默认为ABCDEF为大写字母.
	 * @param bytes source
	 * @return 字符串
	 */
	public static String encodeHex(byte[] bytes) {
		return String.valueOf(encodeHex(bytes, HEX_CHARS));
	}

	public static char[] encodeHex(final byte[] data, final char[] toDigits) {
		final int l = data.length;
		final char[] out = new char[l << 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
			out[j++] = toDigits[0x0F & data[i]];
		}
		return out;
	}

	/**
	 * Hex解码, 将String解码为byte[].
	 * <p>
	 * 字符串有异常时抛出IllegalArgumentException.
	 * @param str 字符串
	 * @return byte数组
	 */
	public static byte[] decodeHex(final String str) {
		char[] data = str.toCharArray();
		final int len = data.length;
		if ((len & 0x01) != 0) {
			throw new RuntimeException("Odd number of characters.");
		}
		final byte[] out = new byte[len >> 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; j < len; i++) {
			int f = toDigit(data[j], j) << 4;
			j++;
			f = f | toDigit(data[j], j);
			j++;
			out[i] = (byte) (f & 0xFF);
		}
		return out;
	}

	public static String encodeBase64(byte[] input) {
		return new String(Base64.getEncoder().encode(input), StandardCharsets.UTF_8);
	}

	public static byte[] decodeBase64(String input) {
		return decodeBase64(input.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Base64解码.
	 * @param input 输入值
	 * @return byte数组
	 */
	public static byte[] decodeBase64(byte[] input) {
		return Base64.getDecoder().decode(input);
	}

	/**
	 * Base64编码, URL安全.(将Base64中的URL非法字符'+'和'/'转为'-'和'_', RFC4648_URLSAFE).
	 * @param input 输入值
	 * @return 字符串
	 */
	public static String encodeBase64UrlSafe(byte[] input) {
		return new String(Base64.getUrlEncoder().encode(input), StandardCharsets.UTF_8);
	}

	/**
	 * Base64解码, URL安全(将Base64中的URL非法字符'+'和'/'转为'-'和'_', RFC4648_URLSAFE).
	 * 如果字符不合法，抛出IllegalArgumentException
	 * @param input 输入
	 * @return 结果
	 */
	public static byte[] decodeBase64UrlSafe(byte[] input) {
		return Base64.getUrlDecoder().decode(input);
	}

	protected static int toDigit(final char ch, final int index) {
		final int digit = Character.digit(ch, 16);
		if (digit == -1) {
			throw new RuntimeException("Illegal hexadecimal character " + ch + " at index " + index);
		}
		return digit;
	}

}
