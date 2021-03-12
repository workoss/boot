/*
 * Copyright 2019-2021 workoss (https://www.workoss.com)
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

import com.workoss.boot.util.ExceptionUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

/**
 * @author admin
 */
@SuppressWarnings("ALL")
public class EscapeUtil {

	/**
	 * URL 编码, Encode默认为UTF-8.
	 * <p>
	 * 转义后的URL可作为URL中的参数
	 * @param part part
	 * @return urlencode
	 */
	public static String urlEncode(String part) {
		try {
			return URLEncoder.encode(part, StandardCharsets.UTF_8.name());
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(ExceptionUtils.toString(e));
		}
	}

	public static String urlDecode(String part) {
		try {
			return URLDecoder.decode(part, StandardCharsets.UTF_8.name());
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(ExceptionUtils.toString(e));
		}
	}

	private static BitSet dontNeedEncoding;

	static {
		dontNeedEncoding = new BitSet(256);
		int i;
		for (i = 'a'; i <= 'z'; i++) {
			dontNeedEncoding.set(i);
		}
		for (i = 'A'; i <= 'Z'; i++) {
			dontNeedEncoding.set(i);
		}
		for (i = '0'; i <= '9'; i++) {
			dontNeedEncoding.set(i);
		}
		dontNeedEncoding.set('+');
		/**
		 * 这里会有误差,比如输入一个字符串 123+456,它到底是原文就是123+456还是123 456做了urlEncode后的内容呢？<br>
		 * 其实问题是一样的，比如遇到123%2B456,它到底是原文即使如此，还是123+456 urlEncode后的呢？ <br>
		 * 在这里，我认为只要符合urlEncode规范的，就当作已经urlEncode过了<br>
		 * 毕竟这个方法的初衷就是判断string是否urlEncode过<br>
		 */

		dontNeedEncoding.set('-');
		dontNeedEncoding.set('_');
		dontNeedEncoding.set('.');
		dontNeedEncoding.set('*');
	}

	public static boolean hasUrlEncoded(String str) {

		/**
		 * 支持JAVA的URLEncoder.encode出来的string做判断。 即: 将' '转成'+' <br>
		 * 0-9a-zA-Z保留 <br>
		 * '-'，'_'，'.'，'*'保留 <br>
		 * 其他字符转成%XX的格式，X是16进制的大写字符，范围是[0-9A-F]
		 */
		boolean needEncode = false;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (dontNeedEncoding.get((int) c)) {
				continue;
			}
			if (c == '%' && (i + 2) < str.length()) {
				// 判断是否符合urlEncode规范
				char c1 = str.charAt(++i);
				char c2 = str.charAt(++i);
				if (isDigit16Char(c1) && isDigit16Char(c2)) {
					continue;
				}
			}
			// 其他字符，肯定需要urlEncode
			needEncode = true;
			break;
		}

		return !needEncode;
	}

	/**
	 * 判断c是否是16进制的字符
	 * @param c
	 * @return
	 */
	private static boolean isDigit16Char(char c) {
		return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F');
	}

	public static String htmlElementContent(String content) {
		if (content == null) {
			return null;
		}
		else {
			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < content.length(); ++i) {
				char c = content.charAt(i);
				if (c == '<') {
					sb.append("&lt;");
				}
				else if (c == '>') {
					sb.append("&gt;");
				}
				else if (c == '\'') {
					sb.append("&#39;");
				}
				else if (c == '&') {
					sb.append("&amp;");
				}
				else if (c == '"') {
					sb.append("&quot;");
				}
				else if (c == '/') {
					sb.append("&#47;");
				}
				else {
					sb.append(c);
				}
			}

			return sb.length() > content.length() ? sb.toString() : content;
		}
	}

	public static String htmlElementContent(Object obj) {
		if (obj == null) {
			return "?";
		}
		else {
			try {
				return htmlElementContent(obj.toString());
			}
			catch (Exception var2) {
				return null;
			}
		}
	}

	public static String xml(String content) {
		return xml((String) null, content);
	}

	public static String xml(String ifNull, String content) {
		return xml(ifNull, false, content);
	}

	public static String xml(String ifNull, boolean escapeCrlf, String content) {
		if (content == null) {
			return ifNull;
		}
		else {
			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < content.length(); ++i) {
				char c = content.charAt(i);
				if (c == '<') {
					sb.append("&lt;");
				}
				else if (c == '>') {
					sb.append("&gt;");
				}
				else if (c == '\'') {
					sb.append("&apos;");
				}
				else if (c == '&') {
					sb.append("&amp;");
				}
				else if (c == '"') {
					sb.append("&quot;");
				}
				else if (escapeCrlf && c == '\r') {
					sb.append("&#13;");
				}
				else if (escapeCrlf && c == '\n') {
					sb.append("&#10;");
				}
				else {
					sb.append(c);
				}
			}

			return sb.length() > content.length() ? sb.toString() : content;
		}
	}

}
