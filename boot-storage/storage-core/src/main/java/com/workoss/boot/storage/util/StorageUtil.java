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
package com.workoss.boot.storage.util;

import com.workoss.boot.util.StringUtils;

/**
 * 对象存储工具类
 *
 * @author workoss
 */
public class StorageUtil {

	public static final String DOT = ".";

	public static final String SLASH = "/";

	public static final String DOUBLE_SLASH = "//";

	private StorageUtil() {
	}

	public static String replaceEndSlash(String url) {
		if (StringUtils.isBlank(url)) {
			return url;
		}
		url = url.trim();
		if (!url.endsWith(StorageUtil.SLASH)) {
			return url;
		}
		return url.substring(0, url.length() - 1);
	}

	public static String replaceStartSlash(String url) {
		if (StringUtils.isBlank(url)) {
			return url;
		}
		url = url.trim();
		if (!url.startsWith(StorageUtil.SLASH)) {
			return url;
		}
		return url.replaceFirst(StorageUtil.SLASH, StringUtils.EMPTY);
	}

	public static String replaceStartEndSlash(String url) {
		url = replaceEndSlash(url);
		return replaceStartSlash(url);
	}

}
