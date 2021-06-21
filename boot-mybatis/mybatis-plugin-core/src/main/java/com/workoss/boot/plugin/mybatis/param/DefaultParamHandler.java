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
		} else {
			AbstractFieldAccess fieldAccess = ReflectUtils.getFieldAccessCache(parameter.getClass());
			String[] fieldNames = fieldAccess.getFieldNames();
			if (fieldNames == null) {
				return;
			}
			Map<String, Object> parameterMap = new MapperMethod.ParamMap<>();
			parameterMap.put("_dbType", dbType);
			for (String fieldName : fieldNames) {
				parameterMap.put(fieldName, ReflectUtils.getPropertyByInvokeMethod(parameter, fieldName));
			}
			parameter = parameterMap;
		}
	}
}
