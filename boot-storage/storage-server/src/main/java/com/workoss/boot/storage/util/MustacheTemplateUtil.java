/*
 * Copyright 2019-2023 workoss (https://www.workoss.com)
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

import com.samskivert.mustache.Mustache;
import com.workoss.boot.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * mustache模板工具类
 *
 * @author workoss
 */
@SuppressWarnings("unused")
public class MustacheTemplateUtil {

	private static final Mustache.Compiler INSTANCE = Mustache.compiler().escapeHTML(false);

	public static String render(String template, Map<String, String> map) {
		if (StringUtils.isBlank(template)) {
			return null;
		}
		if (map == null) {
			map = new HashMap<>(8);
		}
		return INSTANCE.compile(template).execute(map);
	}

	public static String render(String template, Mustache.Formatter formatter, Object object) {
		if (StringUtils.isBlank(template)) {
			return null;
		}
		if (object == null) {
			object = new HashMap<>(8);
		}
		return INSTANCE.withFormatter(formatter).compile(template).execute(object);
	}

	public static String render(String template, Object object) {
		if (StringUtils.isBlank(template)) {
			return null;
		}
		if (object == null) {
			object = new HashMap<>(8);
		}
		return INSTANCE.compile(template).execute(object);
	}

}
