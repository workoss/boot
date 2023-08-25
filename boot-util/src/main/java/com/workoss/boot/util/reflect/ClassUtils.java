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

import com.workoss.boot.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.Closeable;
import java.io.Externalizable;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * class 工具类
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
public class ClassUtils {

	private static final Logger log = LoggerFactory.getLogger(ClassUtils.class);

	public static final String ARRAY_SUFFIX = "[]";

	/**
	 * Prefix for internal array class names: {@code "["}.
	 */
	private static final String INTERNAL_ARRAY_PREFIX = "[";

	/**
	 * Prefix for internal non-primitive array class names: {@code "[L"}.
	 */
	private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";

	/**
	 * A reusable empty class array constant.
	 */
	private static final Class<?>[] EMPTY_CLASS_ARRAY = {};

	/**
	 * The package separator character: {@code '.'}.
	 */
	private static final char PACKAGE_SEPARATOR = '.';

	/**
	 * The path separator character: {@code '/'}.
	 */
	private static final char PATH_SEPARATOR = '/';

	/**
	 * The inner class separator character: {@code '$'}.
	 */
	private static final char INNER_CLASS_SEPARATOR = '$';

	private static final String CGLIB_CLASS_SEPARATOR = "$$";

	public static final String CLASS_FILE_SUFFIX = ".class";

	private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(8);

	private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<>(8);

	private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<>(32);

	private static final Map<String, Class<?>> commonClassCache = new HashMap<>(64);

	static {
		primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
		primitiveWrapperTypeMap.put(Byte.class, byte.class);
		primitiveWrapperTypeMap.put(Character.class, char.class);
		primitiveWrapperTypeMap.put(Double.class, double.class);
		primitiveWrapperTypeMap.put(Float.class, float.class);
		primitiveWrapperTypeMap.put(Integer.class, int.class);
		primitiveWrapperTypeMap.put(Long.class, long.class);
		primitiveWrapperTypeMap.put(Short.class, short.class);
		primitiveWrapperTypeMap.put(Void.class, void.class);

		// Map entry iteration is less expensive to initialize than forEach with lambdas
		for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
			primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
			registerCommonClasses(entry.getKey());
		}

		Set<Class<?>> primitiveTypes = new HashSet<>(32);
		primitiveTypes.addAll(primitiveWrapperTypeMap.values());
		Collections.addAll(primitiveTypes, boolean[].class, byte[].class, char[].class, double[].class, float[].class,
				int[].class, long[].class, short[].class);
		for (Class<?> primitiveType : primitiveTypes) {
			primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
		}

		registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class, Float[].class,
				Integer[].class, Long[].class, Short[].class);
		registerCommonClasses(Number.class, Number[].class, String.class, String[].class, Class.class, Class[].class,
				Object.class, Object[].class);
		registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class, Error.class,
				StackTraceElement.class, StackTraceElement[].class);
		registerCommonClasses(Enum.class, Iterable.class, Iterator.class, Enumeration.class, Collection.class,
				List.class, Set.class, Map.class, Map.Entry.class, Optional.class);

		Class<?>[] javaLanguageInterfaceArray = { Serializable.class, Externalizable.class, Closeable.class,
				AutoCloseable.class, Cloneable.class, Comparable.class };
		registerCommonClasses(javaLanguageInterfaceArray);
	}

	public static Class unwrapCglib(Object root) {
		Assert.notNull(root, "Instance must not be null");
		Class<?> clazz = root.getClass();
		if ((clazz != null) && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
			Class<?> superClass = clazz.getSuperclass();
			if ((superClass != null) && !Object.class.equals(superClass)) {
				return superClass;
			}
		}
		return clazz;
	}

	public static Class<?> getGenericType(Class<?> clazz) {
		if (clazz == Object.class) {
			return null;
		}
		Type type = clazz.getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			ParameterizedType ptype = ((ParameterizedType) type);
			Type[] args = ptype.getActualTypeArguments();
			return (Class<?>) args[0];
		}
		return getGenericType(clazz.getSuperclass());
	}

	public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotation) {
		Annotation ann = clazz.getAnnotation(annotation);
		return ann != null ? (T) ann
				: (clazz.getSuperclass() != Object.class ? getAnnotation(clazz.getSuperclass(), annotation) : (T) ann);
	}

	public static <T extends Annotation> T getAnnotation(Method method, Class<T> annotation) {
		Annotation ann = method.getAnnotation(annotation);
		if (ann != null) {
			return (T) ann;
		}
		else {
			Class clazz = method.getDeclaringClass();
			Class superClass = clazz.getSuperclass();
			if (superClass != Object.class) {
				try {
					Method e = superClass.getMethod(method.getName(), method.getParameterTypes());
					return getAnnotation(e, annotation);
				}
				catch (NoSuchMethodException var6) {
					return null;
				}
			}
			else {
				return (T) ann;
			}
		}
	}

	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		}
		catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = ClassUtils.class.getClassLoader();
			if (cl == null) {
				// getClassLoader() returning null indicates the bootstrap
				// ClassLoader
				try {
					cl = ClassLoader.getSystemClassLoader();
				}
				catch (Throwable ex) {
					// Cannot access message ClassLoader - oh well, maybe the
					// caller can live with null...
				}
			}
		}
		return cl;
	}

	public static boolean isPrimitive(Class<?> clazz) {
		if (primitiveWrapperTypeMap.containsKey(clazz)) {
			return true;
		}
		return clazz.isPrimitive();
	}

	private static void registerCommonClasses(Class<?>... commonClasses) {
		for (Class<?> clazz : commonClasses) {
			commonClassCache.put(clazz.getName(), clazz);
		}
	}

	public static Class<?> resolvePrimitiveClassName(@Nullable String name) {
		Class<?> result = null;
		// Most class names will be quite long, considering that they
		// SHOULD sit in a package, so a length check is worthwhile.
		if (name != null && name.length() <= 7) {
			// Could be a primitive - likely.
			result = primitiveTypeNameMap.get(name);
		}
		return result;
	}

	/**
	 * 根据类名加载Class
	 * @param className 类名
	 * @param initialize 是否初始化
	 * @return class
	 */
	public static Class forName(String className, boolean initialize) {
		try {
			return Class.forName(className, initialize, ClassLoaderUtils.getCurrentClassLoader());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Class<?> forName(String name, @Nullable ClassLoader classLoader)
			throws ClassNotFoundException, LinkageError {

		Assert.notNull(name, "Name must not be null");

		Class<?> clazz = resolvePrimitiveClassName(name);
		if (clazz == null) {
			clazz = commonClassCache.get(name);
		}
		if (clazz != null) {
			return clazz;
		}

		// "java.lang.String[]" style arrays
		if (name.endsWith(ARRAY_SUFFIX)) {
			String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
			Class<?> elementClass = forName(elementClassName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		// "[Ljava.lang.String;" style arrays
		if (name.startsWith(NON_PRIMITIVE_ARRAY_PREFIX) && name.endsWith(";")) {
			String elementName = name.substring(NON_PRIMITIVE_ARRAY_PREFIX.length(), name.length() - 1);
			Class<?> elementClass = forName(elementName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		// "[[I" or "[[Ljava.lang.String;" style arrays
		if (name.startsWith(INTERNAL_ARRAY_PREFIX)) {
			String elementName = name.substring(INTERNAL_ARRAY_PREFIX.length());
			Class<?> elementClass = forName(elementName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		ClassLoader clToUse = classLoader;
		if (clToUse == null) {
			clToUse = getDefaultClassLoader();
		}
		try {
			return Class.forName(name, false, clToUse);
		}
		catch (ClassNotFoundException ex) {
			int lastDotIndex = name.lastIndexOf(PACKAGE_SEPARATOR);
			if (lastDotIndex != -1) {
				String nestedClassName = name.substring(0, lastDotIndex) + INNER_CLASS_SEPARATOR
						+ name.substring(lastDotIndex + 1);
				try {
					return Class.forName(nestedClassName, false, clToUse);
				}
				catch (ClassNotFoundException ex2) {
					// Swallow - let original exception get through
				}
			}
			throw ex;
		}
	}

	public static boolean isPresent(String className, @Nullable ClassLoader classLoader) {
		try {
			forName(className, classLoader);
			return true;
		}
		catch (IllegalAccessError err) {
			throw new IllegalStateException(
					"Readability mismatch in inheritance hierarchy of class [" + className + "]: " + err.getMessage(),
					err);
		}
		catch (Throwable ex) {
			// Typically ClassNotFoundException or NoClassDefFoundError...
			return false;
		}
	}

	/**
	 * 实例化一个对象(只检测默认构造函数，其它不管）
	 * @param clazz 对象类
	 * @param <T> 对象具体类
	 * @return 对象实例
	 * @throws RuntimeException 没有找到方法，或者无法处理，或者初始化方法异常等
	 */
	public static <T> T newInstance(Class<T> clazz) throws RuntimeException {
		if (clazz.isPrimitive()) {
			return (T) getDefaultPrimitiveValue(clazz);
		}

		T t = getDefaultWrapperValue(clazz);
		if (t != null) {
			return t;
		}

		try {
			// 普通类，如果是成员类（需要多传一个父类参数）
			if (!(clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers()))) {
				try {
					// 先找一个空的构造函数
					Constructor<T> constructor = clazz.getDeclaredConstructor();
					constructor.setAccessible(true);
					return constructor.newInstance();
				}
				catch (Exception ignore) { // NOPMD
				}
			}
			// 不行的话，找一个最少参数的构造函数
			Constructor<T>[] constructors = (Constructor<T>[]) clazz.getDeclaredConstructors();
			if (constructors == null || constructors.length == 0) {
				throw new RuntimeException("The " + clazz.getCanonicalName() + " has no default constructor!");
			}
			Constructor<T> constructor = constructors[0];
			if (constructor.getParameterTypes().length > 0) {
				for (Constructor<T> c : constructors) {
					if (c.getParameterTypes().length < constructor.getParameterTypes().length) {
						constructor = c;
						if (constructor.getParameterTypes().length == 0) {
							break;
						}
					}
				}
			}
			constructor.setAccessible(true);
			// 虚拟构造函数的参数值，基本类型使用默认值，其它类型使用null
			Class<?>[] argTypes = constructor.getParameterTypes();
			Object[] args = new Object[argTypes.length];
			for (int i = 0; i < args.length; i++) {
				args[i] = getDefaultPrimitiveValue(argTypes[i]);
			}
			return constructor.newInstance(args);
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 实例化一个对象(根据参数自动检测构造方法）
	 * @param clazz 对象类
	 * @param argTypes 构造函数需要的参数
	 * @param args 构造函数需要的参数
	 * @param <T> 对象具体类
	 * @return 对象实例
	 * @throws RuntimeException 没有找到方法，或者无法处理，或者初始化方法异常等
	 */
	public static <T> T newInstanceWithArgs(Class<T> clazz, Class<?>[] argTypes, Object[] args)
			throws RuntimeException {
		if (argTypes == null || argTypes.length == 0) {
			return newInstance(clazz);
		}
		try {
			if (!(clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers()))) {
				Constructor<T> constructor = clazz.getDeclaredConstructor(argTypes);
				constructor.setAccessible(true);
				return constructor.newInstance(args);
			}
			else {
				Constructor<T>[] constructors = (Constructor<T>[]) clazz.getDeclaredConstructors();
				if (constructors == null || constructors.length == 0) {
					throw new RuntimeException("The " + clazz.getCanonicalName() + " has no constructor with argTypes :"
							+ Arrays.toString(argTypes));
				}
				Constructor<T> constructor = null;
				for (Constructor<T> c : constructors) {
					Class[] ps = c.getParameterTypes();
					// 长度多一
					if (ps.length == argTypes.length + 1) {
						boolean allMath = true;
						// 而且第二个开始的参数类型匹配
						for (int i = 1; i < ps.length; i++) {
							if (ps[i] != argTypes[i - 1]) {
								allMath = false;
								break;
							}
						}
						if (allMath) {
							constructor = c;
							break;
						}
					}
				}
				if (constructor == null) {
					throw new RuntimeException("The " + clazz.getCanonicalName() + " has no constructor with argTypes :"
							+ Arrays.toString(argTypes));
				}
				else {
					constructor.setAccessible(true);
					Object[] newArgs = new Object[args.length + 1];
					System.arraycopy(args, 0, newArgs, 1, args.length);
					return constructor.newInstance(newArgs);
				}
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 得到基本类型的默认值
	 * @param clazz Class类
	 * @return 默认值
	 */
	public static Object getDefaultPrimitiveValue(Class clazz) {
		if (clazz == int.class) {
			return 0;
		}
		else if (clazz == boolean.class) {
			return false;
		}
		else if (clazz == long.class) {
			return 0L;
		}
		else if (clazz == byte.class) {
			return (byte) 0;
		}
		else if (clazz == double.class) {
			return 0d;
		}
		else if (clazz == short.class) {
			return (short) 0;
		}
		else if (clazz == float.class) {
			return 0f;
		}
		else if (clazz == char.class) {
			return (char) 0;
		}
		else {
			return null;
		}
	}

	/**
	 * 得到包装类的默认值
	 * @param clazz Class类
	 * @param <T> 泛型
	 * @return 默认值
	 */
	public static <T> T getDefaultWrapperValue(Class<T> clazz) {
		if (clazz == Short.class) {
			return (T) Short.valueOf((short) 0);
		}
		else if (clazz == Integer.class) {
			return (T) Integer.valueOf(0);
		}
		else if (clazz == Long.class) {
			return (T) Long.valueOf(0L);
		}
		else if (clazz == Double.class) {
			return (T) Double.valueOf(0d);
		}
		else if (clazz == Float.class) {
			return (T) Float.valueOf(0f);
		}
		else if (clazz == Byte.class) {
			return (T) Byte.valueOf((byte) 0);
		}
		else if (clazz == Character.class) {
			return (T) Character.valueOf((char) 0);
		}
		else if (clazz == Boolean.class) {
			return (T) Boolean.FALSE;
		}
		return null;
	}

	/**
	 * Class[]转String[] <br>
	 * 注意，得到的String可能不能直接用于Class.forName，请使用getClasses(String[])反向获取
	 * @param types Class[]
	 * @return 对象描述
	 */
	public static String[] getTypeStrs(Class[] types) {
		return getTypeStrs(types, false);
	}

	/**
	 * Class[]转String[] <br>
	 * 注意，得到的String可能不能直接用于Class.forName，请使用getClasses(String[])反向获取
	 * @param types Class[]
	 * @param javaStyle JDK自带格式，例如 int[], true的话返回 [I; false的话返回int[]
	 * @return 对象描述
	 */
	public static String[] getTypeStrs(Class[] types, boolean javaStyle) {
		if (types == null || types.length == 0) {
			return StringUtils.EMPTY_STRING_ARRAY;
		}
		else {
			String[] strings = new String[types.length];
			for (int i = 0; i < types.length; i++) {
				strings[i] = javaStyle ? types[i].getName() : getTypeStr(types[i]);
			}
			return strings;
		}
	}

	/**
	 * Class转String<br>
	 * 注意，得到的String可能不能直接用于Class.forName，请使用getClass(String)反向获取
	 * @param clazz Class
	 * @return 对象
	 */
	public static String getTypeStr(Class clazz) {
		String typeStr = ReflectUtils.getTypeStrCache(clazz);
		if (typeStr == null) {
			if (clazz.isArray()) {
				// 原始名字：[Ljava.lang.String;
				String name = clazz.getName();
				// java.lang.String[]
				typeStr = jvmNameToCanonicalName(name);
			}
			else {
				typeStr = clazz.getName();
			}
			ReflectUtils.putTypeStrCache(clazz, typeStr);
		}
		return typeStr;
	}

	/**
	 * JVM描述转通用描述
	 * @param jvmName 例如 [I;
	 * @return 通用描述 例如 int[]
	 */
	public static String jvmNameToCanonicalName(String jvmName) {
		boolean isArray = jvmName.charAt(0) == '[';
		if (isArray) {
			// 计数，看上几维数组
			String cnName = StringUtils.EMPTY;
			int i = 0;
			for (; i < jvmName.length(); i++) {
				if (jvmName.charAt(i) != '[') {
					break;
				}
				cnName += "[]";
			}
			String componentType = jvmName.substring(i, jvmName.length());
			if ("Z".equals(componentType)) {
				cnName = "boolean" + cnName;
			}
			else if ("B".equals(componentType)) {
				cnName = "byte" + cnName;
			}
			else if ("C".equals(componentType)) {
				cnName = "char" + cnName;
			}
			else if ("D".equals(componentType)) {
				cnName = "double" + cnName;
			}
			else if ("F".equals(componentType)) {
				cnName = "float" + cnName;
			}
			else if ("I".equals(componentType)) {
				cnName = "int" + cnName;
			}
			else if ("J".equals(componentType)) {
				cnName = "long" + cnName;
			}
			else if ("S".equals(componentType)) {
				cnName = "short" + cnName;
			}
			else {
				// 对象的// 去掉L
				cnName = componentType.substring(1, componentType.length() - 1) + cnName;

			}
			return cnName;
		}
		return jvmName;
	}

	public static String getShortName(String className) {
		Assert.hasLength(className, "Class name must not be empty");
		int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		int nameEndIndex = className.indexOf(CGLIB_CLASS_SEPARATOR);
		if (nameEndIndex == -1) {
			nameEndIndex = className.length();
		}
		String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
		shortName = shortName.replace(INNER_CLASS_SEPARATOR, PACKAGE_SEPARATOR);
		return shortName;
	}

	public static String getShortName(Class<?> clazz) {
		return getShortName(clazz.getTypeName());
	}

}
