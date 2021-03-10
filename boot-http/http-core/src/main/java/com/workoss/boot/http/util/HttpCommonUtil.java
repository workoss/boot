/*
 * Copyright © 2020-2021 workoss (WORKOSS)
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
package com.workoss.boot.http.util;

import com.workoss.boot.annotation.lang.Nullable;
import com.workoss.boot.util.StringUtils;

import java.util.Map;

public class HttpCommonUtil {

	private static final String SEPARATOR_CHARS = new String(
			new char[] { '(', ')', '<', '>', '@', ',', ';', ':', '\\', '"', '/', '[', ']', '?', '=', '{', '}', ' ' });

	private static final String DOMAIN_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.-";

	public static void validateCookieName(String name) {
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			// CTL = <US-ASCII control chars (octets 0 - 31) and DEL (127)>
			if (c <= 0x1F || c == 0x7F) {
				throw new IllegalArgumentException(name + ": RFC2616 token cannot have control chars");
			}
			if (SEPARATOR_CHARS.indexOf(c) >= 0) {
				throw new IllegalArgumentException(
						name + ": RFC2616 token cannot have separator chars such as '" + c + "'");
			}
			if (c >= 0x80) {
				throw new IllegalArgumentException(
						name + ": RFC2616 token can only have US-ASCII: 0x" + Integer.toHexString(c));
			}
		}
	}

	public static void validateCookieValue(@Nullable String value) {
		if (value == null) {
			return;
		}
		int start = 0;
		int end = value.length();
		if (end > 1 && value.charAt(0) == '"' && value.charAt(end - 1) == '"') {
			start = 1;
			end--;
		}
		for (int i = start; i < end; i++) {
			char c = value.charAt(i);
			if (c < 0x21 || c == 0x22 || c == 0x2c || c == 0x3b || c == 0x5c || c == 0x7f) {
				throw new IllegalArgumentException("RFC2616 cookie value cannot have '" + c + "'");
			}
			if (c >= 0x80) {
				throw new IllegalArgumentException(
						"RFC2616 cookie value can only have US-ASCII chars: 0x" + Integer.toHexString(c));
			}
		}
	}

	public static void validateDomain(@Nullable String domain) {
		if (!StringUtils.hasLength(domain)) {
			return;
		}
		int char1 = domain.charAt(0);
		int charN = domain.charAt(domain.length() - 1);
		if (char1 == '-' || charN == '.' || charN == '-') {
			throw new IllegalArgumentException("Invalid first/last char in cookie domain: " + domain);
		}
		for (int i = 0, c = -1; i < domain.length(); i++) {
			int p = c;
			c = domain.charAt(i);
			if (DOMAIN_CHARS.indexOf(c) == -1 || (p == '.' && (c == '.' || c == '-')) || (p == '-' && c == '.')) {
				throw new IllegalArgumentException(domain + ": invalid cookie domain char '" + c + "'");
			}
		}
	}

	public static void validatePath(@Nullable String path) {
		if (path == null) {
			return;
		}
		for (int i = 0; i < path.length(); i++) {
			char c = path.charAt(i);
			if (c < 0x20 || c > 0x7E || c == ';') {
				throw new IllegalArgumentException(path + ": Invalid cookie path char '" + c + "'");
			}
		}
	}

	public static String renderUrl(String templateUrl, Map<String, String> uriParam) {
		return StringUtils.renderString(templateUrl, "{", "}", uriParam, "");
	}

}
