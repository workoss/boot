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
package com.workoss.boot.util.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import com.workoss.boot.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.objenesis.ObjenesisStd;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

/**
 * 反射工具类
 *
 * @author workoss
 */
public class ReflectUtils {

	private static final Logger log = LoggerFactory.getLogger(ReflectUtils.class);

	private static final String SETTER_PREFIX = "set";

	private static final String GETTER_PREFIX = "get";

	private static final String CACHE_PREFIX = "reflect_";

	private static final ObjenesisStd OBJENESIS_STD = new ObjenesisStd(true);

	/**
	 * 应用对应的ClassLoader
	 */
	static final ConcurrentMap<String, ClassLoader> APPNAME_CLASSLOADER_MAP = new ConcurrentHashMap<String, ClassLoader>();

	/**
	 * 服务对应的ClassLoader
	 */
	static final ConcurrentMap<String, ClassLoader> SERVICE_CLASSLOADER_MAP = new ConcurrentHashMap<String, ClassLoader>();

	private static Map<String, AbstractMethodAccess> methodAccessCache = new ConcurrentHashMap<>();

	private static Map<String, AbstractFieldAccess> fieldAccessCache = new ConcurrentHashMap<>();

	private static Map<String, Map<String, Object>> classMethodCache = new ConcurrentHashMap<>();

	private static final ConcurrentMap<Class, String> TYPE_STR_CACHE = new ConcurrentHashMap<Class, String>();

	public static <T> T newInstance(Class<T> clazz) {
		ObjectInstantiator objectInstantiator = OBJENESIS_STD.getInstantiatorOf(clazz);
		return (T) objectInstantiator.newInstance();
	}

	public static Object invokeMethod(Object obj, String methodName, Object param) {
		if (!hasMethod(obj.getClass(), methodName)) {
			log.warn("{} 没有找到方法 {}", obj.getClass(), methodName);
			return null;
		}
		AbstractFieldAccess.get(obj.getClass()).getFieldNames();
		AbstractMethodAccess methodAccess = getMethodAccessCache(obj.getClass());
		if (param == null) {
			return methodAccess.invoke(obj, methodAccess.getIndex(methodName));
		}
		return methodAccess.invoke(obj, methodAccess.getIndex(methodName), param);
	}

	public static Object getPropertyByInvokeMethod(Object obj, String property) {
		String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(property);
		return invokeMethod(obj, getterMethodName, null);
	}

	public static void setPropertyByInvokeMethod(Object obj, String property, Object value) {
		String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(property);
		invokeMethod(obj, setterMethodName, value);
	}

	public static boolean hasMethod(Class clazz, String methodName) {
		Map<String, Object> map = classMethodCache.get(clazz.getName());
		if (map == null) {
			AbstractMethodAccess methodAccess = getMethodAccessCache(clazz);
			String[] methodNames = methodAccess.getMethodNames();
			if (methodNames != null && methodNames.length > 0) {
				map = Arrays.stream(methodNames)
					.distinct()
					.filter(name -> !isClassDefaultMethod(name))
					.collect(Collectors.toMap(name -> name, name -> true));
			}
			classMethodCache.put(clazz.getName(), map);
		}
		return map.containsKey(methodName);
	}

	public static boolean isClassDefaultMethod(String methodName) {
		return "equals".equalsIgnoreCase(methodName) || "toString".equalsIgnoreCase(methodName)
				|| "hashCode".equalsIgnoreCase(methodName) || "canEqual".equalsIgnoreCase(methodName);
	}

	/**
	 * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
	 * @param obj 对象
	 * @param fieldName 属性名
	 * @return obj对象
	 */
	public static Object getFieldValue(final Object obj, final String fieldName) {
		Field field = getAccessibleField(obj, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
		}

		Object result = null;
		try {
			result = field.get(obj);
		}
		catch (IllegalAccessException e) {
			log.error("不可能抛出的异常{}", e.getMessage());
		}
		return result;
	}

	/**
	 * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
	 * @param obj 对象
	 * @param fieldName 属性名
	 * @param value 属性值
	 */
	public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
		Field field = getAccessibleField(obj, fieldName);
		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
		}
		try {
			field.set(obj, value);
		}
		catch (IllegalAccessException e) {
			log.error("不可能抛出的异常:{}", e.getMessage());
		}
	}

	public static AbstractMethodAccess getMethodAccessCache(Class obj) {
		AbstractMethodAccess methodAccess = methodAccessCache.get(CACHE_PREFIX + obj.getName());
		if (methodAccess == null) {
			methodAccess = AbstractMethodAccess.get(obj);
			methodAccessCache.put(CACHE_PREFIX + obj.getName(), methodAccess);
			return methodAccess;
		}
		return methodAccess;
	}

	public static AbstractFieldAccess getFieldAccessCache(Class obj) {
		AbstractFieldAccess fieldAccess = fieldAccessCache.get(CACHE_PREFIX + obj.getName());
		if (fieldAccess == null) {
			fieldAccess = AbstractFieldAccess.get(obj);
			fieldAccessCache.put(CACHE_PREFIX + obj.getName(), fieldAccess);
			return fieldAccess;
		}
		return fieldAccess;
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
	 * <p>
	 * @param obj 对象
	 * @param fieldName 属性名
	 * @return field
	 */
	public static Field getAccessibleField(final Object obj, final String fieldName) {
		if (obj == null || fieldName == null) {
			throw new RuntimeException("object fieldName not null");
		}
		for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass
			.getSuperclass()) {
			try {
				Field field = superClass.getDeclaredField(fieldName);
				makeAccessible(field);
				return field;
			}
			catch (NoSuchFieldException e) {// NOSONAR
				// Field不在当前类定义,继续向上转型
			}
		}
		return null;
	}

	public static void makeAccessible(Field field) {
		boolean notPublic = !Modifier.isPublic(field.getModifiers())
				|| !Modifier.isPublic(field.getDeclaringClass().getModifiers())
				|| Modifier.isFinal(field.getModifiers());
		if (notPublic && !field.isAccessible()) {
			field.setAccessible(true);
		}
	}

	/**
	 * 注册服务所在的ClassLoader
	 * @param appName 应用名
	 * @param classloader 应用级别ClassLoader
	 */
	public static void registerAppClassLoader(String appName, ClassLoader classloader) {
		APPNAME_CLASSLOADER_MAP.put(appName, classloader);
	}

	/**
	 * 得到服务的自定义ClassLoader
	 * @param appName 应用名
	 * @return 应用级别ClassLoader
	 */
	public static ClassLoader getAppClassLoader(String appName) {
		ClassLoader appClassLoader = APPNAME_CLASSLOADER_MAP.get(appName);
		if (appClassLoader == null) {
			return ClassLoaderUtils.getCurrentClassLoader();
		}
		else {
			return appClassLoader;
		}
	}

	/**
	 * 注册服务所在的ClassLoader
	 * @param serviceUniqueName 服务唯一名称
	 * @param classloader 服务级别ClassLoader
	 */
	public static void registerServiceClassLoader(String serviceUniqueName, ClassLoader classloader) {
		SERVICE_CLASSLOADER_MAP.put(serviceUniqueName, classloader);
	}

	/**
	 * 得到服务的自定义ClassLoader
	 * @param serviceUniqueName 服务唯一名称
	 * @return 服务级别ClassLoader
	 */
	public static ClassLoader getServiceClassLoader(String serviceUniqueName) {
		ClassLoader appClassLoader = SERVICE_CLASSLOADER_MAP.get(serviceUniqueName);
		if (appClassLoader == null) {
			return ClassLoaderUtils.getCurrentClassLoader();
		}
		else {
			return appClassLoader;
		}
	}

	/**
	 * 放入类描述缓存
	 * @param clazz 类
	 * @param typeStr 对象描述
	 */
	public static void putTypeStrCache(Class clazz, String typeStr) {
		TYPE_STR_CACHE.put(clazz, typeStr);
	}

	/**
	 * 得到类描述缓存
	 * @param clazz 类
	 * @return 类描述
	 */
	public static String getTypeStrCache(Class clazz) {
		return TYPE_STR_CACHE.get(clazz);
	}

}
