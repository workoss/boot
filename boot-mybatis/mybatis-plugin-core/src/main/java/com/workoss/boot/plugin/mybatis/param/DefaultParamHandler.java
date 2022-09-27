/*
 * Copyright 2019-2022 workoss (https://www.workoss.com)
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
package com.workoss.boot.plugin.mybatis.param;

import com.workoss.boot.plugin.mybatis.ParamHandler;
import com.workoss.boot.plugin.mybatis.util.ProviderUtil;
import com.workoss.boot.util.reflect.AbstractFieldAccess;
import com.workoss.boot.util.reflect.ReflectUtils;
import org.apache.ibatis.binding.MapperMethod;

import java.util.Map;

/**
 * 默认参数处理
 *
 * @author workoss
 */
public class DefaultParamHandler implements ParamHandler {

	@Override
	public void handler(Object parameter) {
		String dbType = ProviderUtil.getDbType();
		if (parameter instanceof Map) {
			((Map) parameter).put("_dbType", dbType);
		}
		else {
			String className = parameter.getClass().getName();
			if (className.startsWith("java.") || className.startsWith("javax.")) {
				return;
			}
			AbstractFieldAccess fieldAccess = ReflectUtils.getFieldAccessCache(parameter.getClass());
			String[] fieldNames = fieldAccess.getFieldNames();
			if (fieldNames == null) {
				return;
			}
			parameter = new MapperMethod.ParamMap<>();
			((Map<String, Object>) parameter).put("_dbType", dbType);
			for (String fieldName : fieldNames) {
				((Map<String, Object>) parameter).put(fieldName,
						ReflectUtils.getPropertyByInvokeMethod(parameter, fieldName));
			}
		}
	}

}
