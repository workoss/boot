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
package com.workoss.boot.util.exception;

import com.workoss.boot.util.collection.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * 异常
 *
 * @author workoss
 */

public class ExceptionUtils {

    private static final Map<String, Constructor<? extends BootException>> CONSTRUCTOR_MAP = new ConcurrentHashMap<>();

    private static final Map<String, Constructor<? extends Exception>> COMMON_EXCEPTION_CONSTRUCTOR_MAP = new ConcurrentHashMap<>();

    /**
     * 返回堆栈信息（e.printStackTrace()的内容）
     *
     * @param e Throwable
     * @return 异常堆栈信息
     */
    public static String toString(Throwable e) {
        StackTraceElement[] traces = e.getStackTrace();
        StringBuilder sb = new StringBuilder(1024);
        sb.append(e).append("\n");
        if (traces != null) {
            for (StackTraceElement trace : traces) {
                sb.append("\tat ").append(trace).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * 返回消息+简短堆栈信息（e.printStackTrace()的内容）
     *
     * @param e          Throwable
     * @param stackLevel 堆栈层级
     * @return 异常堆栈信息
     */
    public static String toShortString(Throwable e, int stackLevel) {
        StackTraceElement[] traces = e.getStackTrace();
        StringBuilder sb = new StringBuilder(1024);
        sb.append(e).append("\t");
        if (traces != null) {
            for (int i = 0; i < traces.length; i++) {
                if (i < stackLevel) {
                    sb.append("\tat ").append(traces[i]).append("\t");
                } else {
                    break;
                }
            }
        }
        return sb.toString();
    }


    @SuppressWarnings("unchecked")
    public static <T extends BootException> T newInstance(Class<T> clazz, String code, String msg) {
        try {
            Pair<Integer, Constructor<? extends BootException>> constructorPair = getConstructor(clazz);
            int paramsSize = constructorPair.getFirst();
            if (paramsSize == 2) {
                return (T) constructorPair.getSecond().newInstance(code, msg);
            } else if (paramsSize == 1) {
                return (T) constructorPair.getSecond().newInstance(code);
            }
            return (T) new BootException(code, msg);
        } catch (ReflectiveOperationException e) {
            throw new BootException(e);
        }
    }

    public static Pair<Integer, Constructor<? extends BootException>> getConstructor(Class<? extends BootException> clazz) {
        Constructor<? extends BootException> constructor = getConstructor(clazz, String.class, String.class);
        if (constructor != null) {
            return Pair.of(2, constructor);
        }
        return Pair.of(1, getConstructor(clazz, String.class));
    }

    public static Constructor<? extends BootException> getConstructor(Class<? extends BootException> clazz, Class<?>... paramTypes) {
        return CONSTRUCTOR_MAP.computeIfAbsent(clazz.getName() + (paramTypes == null ? 0 : paramTypes.length), s -> {
            try {
                return clazz.getConstructor(paramTypes);
            } catch (NoSuchMethodException ignored) {
            }
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    public static <T extends Exception> T commonException(Class<T> clazz, Object... params) {
        Constructor<? extends Exception> constructor = COMMON_EXCEPTION_CONSTRUCTOR_MAP.computeIfAbsent(clazz.getName() + (params == null ? 0 : params.length), s -> {
            try {
                if (params == null) {
                    return clazz.getConstructor();
                }
                return clazz.getConstructor(Arrays.stream(params).map(Object::getClass).toArray(Class[]::new));
            } catch (NoSuchMethodException ignored) {
            }
            return null;
        });
        if (constructor == null) {
            throw new RuntimeException("not found exception constructor:" + clazz.getName());
        }
        try {
            return (T) constructor.newInstance(params);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

    }

}
