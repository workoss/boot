/*
 * The MIT License
 * Copyright © 2020-2021 workoss
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.workoss.boot.util.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.workoss.boot.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;

/**
 * class 工具类
 *
 * @author: luanfeng
 */
@SuppressWarnings("ALL")
public class ClassUtils {

	private static final Logger log = LoggerFactory.getLogger(ClassUtils.class);

	/**
	 * The package separator character: {@code '.'}.
	 */
	private static final char PACKAGE_SEPARATOR = '.';

	/**
	 * The inner class separator character: {@code '$'}.
	 */
	private static final char INNER_CLASS_SEPARATOR = '$';

	private static final String CGLIB_CLASS_SEPARATOR = "$$";

	private static final Map<Class<?>, Class<?>> PRIMITIVE_MAP = new HashMap<>(9);

	static {
		PRIMITIVE_MAP.put(String.class, String.class);
		PRIMITIVE_MAP.put(Boolean.class, boolean.class);
		PRIMITIVE_MAP.put(Byte.class, byte.class);
		PRIMITIVE_MAP.put(Character.class, char.class);
		PRIMITIVE_MAP.put(Double.class, double.class);
		PRIMITIVE_MAP.put(Float.class, float.class);
		PRIMITIVE_MAP.put(Integer.class, int.class);
		PRIMITIVE_MAP.put(Long.class, long.class);
		PRIMITIVE_MAP.put(Short.class, short.class);
		PRIMITIVE_MAP.put(Date.class, Date.class);
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
		} else {
			Class clazz = method.getDeclaringClass();
			Class superClass = clazz.getSuperclass();
			if (superClass != Object.class) {
				try {
					Method e = superClass.getMethod(method.getName(), method.getParameterTypes());
					return getAnnotation(e, annotation);
				} catch (NoSuchMethodException var6) {
					return null;
				}
			} else {
				return (T) ann;
			}
		}
	}

	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) {
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
				} catch (Throwable ex) {
					// Cannot access message ClassLoader - oh well, maybe the
					// caller can live with null...
				}
			}
		}
		return cl;
	}

	public static boolean isPrimitive(Class<?> clazz) {
		if (PRIMITIVE_MAP.containsKey(clazz)) {
			return true;
		}
		return clazz.isPrimitive();
	}

	/**
	 * 根据类名加载Class
	 *
	 * @param className  类名
	 * @param initialize 是否初始化
	 * @return Class
	 */
	public static Class forName(String className, boolean initialize) {
		try {
			return Class.forName(className, initialize, ClassLoaderUtils.getCurrentClassLoader());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 实例化一个对象(只检测默认构造函数，其它不管）
	 *
	 * @param clazz 对象类
	 * @param <T>   对象具体类
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
				} catch (Exception ignore) { // NOPMD
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
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 实例化一个对象(根据参数自动检测构造方法）
	 *
	 * @param clazz    对象类
	 * @param argTypes 构造函数需要的参数
	 * @param args     构造函数需要的参数
	 * @param <T>      对象具体类
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
			} else {
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
				} else {
					constructor.setAccessible(true);
					Object[] newArgs = new Object[args.length + 1];
					System.arraycopy(args, 0, newArgs, 1, args.length);
					return constructor.newInstance(newArgs);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 得到基本类型的默认值
	 *
	 * @param clazz Class类
	 * @return 默认值
	 */
	public static Object getDefaultPrimitiveValue(Class clazz) {
		if (clazz == int.class) {
			return 0;
		} else if (clazz == boolean.class) {
			return false;
		} else if (clazz == long.class) {
			return 0L;
		} else if (clazz == byte.class) {
			return (byte) 0;
		} else if (clazz == double.class) {
			return 0d;
		} else if (clazz == short.class) {
			return (short) 0;
		} else if (clazz == float.class) {
			return 0f;
		} else if (clazz == char.class) {
			return (char) 0;
		} else {
			return null;
		}
	}

	/**
	 * 得到包装类的默认值
	 *
	 * @param clazz Class类
	 * @param <T> 泛型
	 * @return 默认值
	 */
	public static <T> T getDefaultWrapperValue(Class<T> clazz) {
		if (clazz == Short.class) {
			return (T) Short.valueOf((short) 0);
		} else if (clazz == Integer.class) {
			return (T) Integer.valueOf(0);
		} else if (clazz == Long.class) {
			return (T) Long.valueOf(0L);
		} else if (clazz == Double.class) {
			return (T) Double.valueOf(0d);
		} else if (clazz == Float.class) {
			return (T) Float.valueOf(0f);
		} else if (clazz == Byte.class) {
			return (T) Byte.valueOf((byte) 0);
		} else if (clazz == Character.class) {
			return (T) Character.valueOf((char) 0);
		} else if (clazz == Boolean.class) {
			return (T) Boolean.FALSE;
		}
		return null;
	}

	/**
	 * Class[]转String[] <br>
	 * 注意，得到的String可能不能直接用于Class.forName，请使用getClasses(String[])反向获取
	 *
	 * @param types Class[]
	 * @return 对象描述
	 */
	public static String[] getTypeStrs(Class[] types) {
		return getTypeStrs(types, false);
	}

	/**
	 * Class[]转String[] <br>
	 * 注意，得到的String可能不能直接用于Class.forName，请使用getClasses(String[])反向获取
	 *
	 * @param types     Class[]
	 * @param javaStyle JDK自带格式，例如 int[], true的话返回 [I; false的话返回int[]
	 * @return 对象描述
	 */
	public static String[] getTypeStrs(Class[] types, boolean javaStyle) {
		if (types == null || types.length == 0) {
			return StringUtils.EMPTY_STRING_ARRAY;
		} else {
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
	 *
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
			} else {
				typeStr = clazz.getName();
			}
			ReflectUtils.putTypeStrCache(clazz, typeStr);
		}
		return typeStr;
	}

	/**
	 * JVM描述转通用描述
	 *
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
			} else if ("B".equals(componentType)) {
				cnName = "byte" + cnName;
			} else if ("C".equals(componentType)) {
				cnName = "char" + cnName;
			} else if ("D".equals(componentType)) {
				cnName = "double" + cnName;
			} else if ("F".equals(componentType)) {
				cnName = "float" + cnName;
			} else if ("I".equals(componentType)) {
				cnName = "int" + cnName;
			} else if ("J".equals(componentType)) {
				cnName = "long" + cnName;
			} else if ("S".equals(componentType)) {
				cnName = "short" + cnName;
			} else {
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
