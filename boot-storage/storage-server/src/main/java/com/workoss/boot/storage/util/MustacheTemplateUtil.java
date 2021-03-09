package com.workoss.boot.storage.util;

import com.samskivert.mustache.Mustache;
import com.yifengx.popeye.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class MustacheTemplateUtil {

	private static final Mustache.Compiler INSTANCE = Mustache.compiler().escapeHTML(false);

	public static String render(String template, Map<String, String> map) {
		if (StringUtils.isBlank(template)) {
			return null;
		}
		if (map == null) {
			map = new HashMap<>();
		}
		return INSTANCE.compile(template).execute(map);
	}

	public static String render(String template, Mustache.Formatter formatter, Object object) {
		if (StringUtils.isBlank(template)) {
			return null;
		}
		if (object == null) {
			object = new HashMap<>();
		}
		return INSTANCE.withFormatter(formatter).compile(template).execute(object);
	}

	public static String render(String template, Object object) {
		if (StringUtils.isBlank(template)) {
			return null;
		}
		if (object == null) {
			object = new HashMap<>();
		}
		return INSTANCE.compile(template).execute(object);
	}

}
