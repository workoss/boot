/*
 * #%L
 * %%
 * Copyright (C) 2019 Workoss Software, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.workoss.boot.util.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;

/**
 * @Description: class 工具类
 * @Author: luanfeng
 * @Date: 2017/8/11 8:10
 * @Version: 1.0.0
 */
public class ClassUtil {
    private static final Logger log= LoggerFactory.getLogger(ClassUtil.class);
    private static final String CGLIB_CLASS_SEPARATOR = "$$";
    private static final Map<Class<?>, Class<?>> primitiveMap = new HashMap<>(9);

    static {
        primitiveMap.put(String.class, String.class);
        primitiveMap.put(Boolean.class, boolean.class);
        primitiveMap.put(Byte.class, byte.class);
        primitiveMap.put(Character.class, char.class);
        primitiveMap.put(Double.class, double.class);
        primitiveMap.put(Float.class, float.class);
        primitiveMap.put(Integer.class, int.class);
        primitiveMap.put(Long.class, long.class);
        primitiveMap.put(Short.class, short.class);
        primitiveMap.put(Date.class, Date.class);
    }

    public static Class unwrapCglib(Object root){
        Assert.notNull(root,"Instance must not be null");
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
        if(clazz == Object.class){
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
        return ann != null? (T) ann :(clazz.getSuperclass() != Object.class?getAnnotation(clazz.getSuperclass(), annotation): (T) ann);
    }

    public static <T extends Annotation> T getAnnotation(Method method, Class<T> annotation) {
        Annotation ann = method.getAnnotation(annotation);
        if(ann != null) {
            return (T) ann;
        } else {
            Class clazz = method.getDeclaringClass();
            Class superClass = clazz.getSuperclass();
            if(superClass != Object.class) {
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
            cl = ClassUtil.class.getClassLoader();
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
        if (primitiveMap.containsKey(clazz)) {
            return true;
        }
        return clazz.isPrimitive();
    }

}
