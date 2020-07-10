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
package com.workoss.boot.util.text;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@SuppressWarnings({ "ALL", "AlibabaUndefineMagicConstant" })
public class BaseEncodeUtil {

	private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };

	/**
	 * Hex编码, 将byte[]编码为String，默认为ABCDEF为大写字母.
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
	 *
	 * 字符串有异常时抛出IllegalArgumentException.
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

	/**
	 * Base64编码.
	 */
	public static String encodeBase64(byte[] input) {
		return new String(Base64.getEncoder().encode(input), StandardCharsets.UTF_8);
	}

	public static byte[] decodeBase64(String input) {
		return decodeBase64(input.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Base64解码.
	 *
	 * 如果字符不合法，抛出IllegalArgumentException
	 */
	public static byte[] decodeBase64(byte[] input) {
		return Base64.getDecoder().decode(input);
	}

	/**
	 * Base64编码, URL安全.(将Base64中的URL非法字符'+'和'/'转为'-'和'_', RFC4648_URLSAFE).
	 */
	public static String encodeBase64UrlSafe(byte[] input) {
		return new String(Base64.getUrlEncoder().encode(input), StandardCharsets.UTF_8);
	}

	/**
	 * Base64解码, URL安全(将Base64中的URL非法字符'+'和'/'转为'-'和'_', RFC4648_URLSAFE).
	 *
	 * 如果字符不合法，抛出IllegalArgumentException
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
