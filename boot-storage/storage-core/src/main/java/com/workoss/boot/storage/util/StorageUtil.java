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
