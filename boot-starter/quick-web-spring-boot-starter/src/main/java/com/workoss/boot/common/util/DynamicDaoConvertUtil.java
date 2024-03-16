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
package com.workoss.boot.common.util;

import com.workoss.boot.plugin.mybatis.DynamicDaoConvert;
import com.workoss.boot.util.json.JsonMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author workoss
 */
public final class DynamicDaoConvertUtil {

	private DynamicDaoConvertUtil() {
	}

	public static <T> DynamicDaoConvert<T> convertFunction(Class<T> tClass) {
		return new DynamicDaoConvert<>() {
			@Override
			public Map<String, Object> convertParam(T t) {
				if (t == null) {
					return Collections.emptyMap();
				}
				if (tClass.isAssignableFrom(Map.class)) {
					return (Map<String, Object>) t;
				}
				return JsonMapper.parseObject(JsonMapper.toJSONBytes(t), Map.class);
			}

			@Override
			public List<T> convertResult(List<Map<String, Object>> result) {
				if (result == null || result.isEmpty()) {
					return Collections.emptyList();
				}
				if (tClass.isAssignableFrom(Map.class)) {
					return (List<T>) result;
				}
				return JsonMapper.parseArray(JsonMapper.toJSONBytes(result), tClass);
			}
		};
	}

}
