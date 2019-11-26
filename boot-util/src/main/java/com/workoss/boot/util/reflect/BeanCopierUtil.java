package com.workoss.boot.util.reflect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;


import com.workoss.boot.util.StringUtils;
import com.workoss.boot.util.collection.CollectionUtils;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;

/**
 * @author: workoss
 * @date: 2018-11-26 12:58
 * @version:
 */
public class BeanCopierUtil {
    private static final ConcurrentMap<String, List<Map<String, String>>> CLASS_METHOD_MAP = new ConcurrentHashMap<>();

    private static final ConcurrentMap<String, BeanCopier> beanCopierMap = new ConcurrentHashMap<>();

    private static final String SETTER_PREFIX = "set";
    private static final String GETTER_PREFIX = "get";


    public static <T> List<T> copy(List<Object> sourceList, Class<T> target) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return Collections.EMPTY_LIST;
        }
        List<T> list = new ArrayList<>(sourceList.size());
        for (Object o : sourceList) {
            list.add(copy(o, target));
        }
        return list;
    }


    public static <T> T copy(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        List<Map<String, String>> getSetList = getGetAndSetMethod(source.getClass(), targetClass);
        if (CollectionUtils.isEmpty(getSetList)) {
            return null;
        }
        T ret = ReflectUtils.newInstance(targetClass);
        for (Map<String, String> stringStringMap : getSetList) {
            Object param = ReflectUtils.invokeMethod(source, stringStringMap.get(GETTER_PREFIX), null);
            if (param == null) {
                continue;
            }
            ReflectUtils.invokeMethod(ret, stringStringMap.get(SETTER_PREFIX), param);
        }

        return ret;
    }


    private static List<Map<String, String>> getGetAndSetMethod(Class source, Class target) {
        String cacheKey = generateBeanKey(source, target);
        if (CLASS_METHOD_MAP.containsKey(cacheKey)) {
            return CLASS_METHOD_MAP.get(cacheKey);
        }

        AbstractMethodAccess targetMethodAccess = ReflectUtils.getMethodAccessCache(target);
        String[] targetMethodNames = targetMethodAccess.getMethodNames();
        if (!(targetMethodNames != null && targetMethodNames.length > 0)) {
            return null;
        }
        Map<String, String> map = Arrays.stream(targetMethodNames).filter(methodName -> {
            boolean ignore = methodName.equalsIgnoreCase("equals") || methodName.equalsIgnoreCase("toString")
                    || methodName.equalsIgnoreCase("hashCode") || methodName.equalsIgnoreCase("canEqual");
            return !ignore && methodName.startsWith(SETTER_PREFIX);
        })
                .collect(Collectors.toMap(str -> StringUtils.uncapitalize(str.replaceAll(SETTER_PREFIX, "")), str -> str));
        if (CollectionUtils.isEmpty(map)) {
            return null;
        }

        AbstractMethodAccess sourceMethodAccess = ReflectUtils.getMethodAccessCache(source);
        String[] sourceMethods = sourceMethodAccess.getMethodNames();
        if (!(sourceMethods != null && sourceMethods.length > 0)) {
            return Collections.emptyList();
        }
        List<Map<String, String>> resultMap = Arrays.stream(sourceMethods)
                .filter(sourceMethod -> {
                    if (!sourceMethod.startsWith(GETTER_PREFIX)) {
                        return false;
                    }
                    String setMethodName = map.get(StringUtils.uncapitalize(sourceMethod.replaceAll(GETTER_PREFIX, "")));
                    return StringUtils.isNotBlank(setMethodName);
                })
                .map(sourceMethod -> {
                    return new HashMap<String, String>() {{
                        put(GETTER_PREFIX, sourceMethod);
                        put(SETTER_PREFIX, map.get(StringUtils.uncapitalize(sourceMethod.replaceAll(GETTER_PREFIX, ""))));
                    }};
                })
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(resultMap)) {
            resultMap = Collections.emptyList();
        }
        CLASS_METHOD_MAP.put(cacheKey, resultMap);
        return resultMap;
    }

    public static <T> T convert(Object source, Class<T> target) {
        T ret = null;
        if (source != null) {
            ret = ReflectUtils.newInstance(target);
            BeanCopier beanCopier = getBeanCopier(source.getClass(), target);
            beanCopier.copy(source, ret, new DeepCopyConverter(target));
        }
        return ret;
    }


    public static BeanCopier getBeanCopier(Class<?> source, Class<?> target) {
        String beanCopierKey = generateBeanKey(source, target);
        if (beanCopierMap.containsKey(beanCopierKey)) {
            return beanCopierMap.get(beanCopierKey);
        } else {
            BeanCopier beanCopier = BeanCopier.create(source, target, true);
            beanCopierMap.putIfAbsent(beanCopierKey, beanCopier);
        }
        return beanCopierMap.get(beanCopierKey);
    }

    /**
     * @param source
     * @param target
     * @return String
     * @description 生成两个类的key
     */
    public static String generateBeanKey(Class<?> source, Class<?> target) {
        return source.getName() + "@" + target.getName();
    }


    public static class DeepCopyConverter implements Converter {

        /**
         * The Target.
         */
        private Class<?> target;

        /**
         * Instantiates a new Deep copy converter.
         *
         * @param target the target
         */
        public DeepCopyConverter(Class<?> target) {
            this.target = target;
        }

        @Override
        public Object convert(Object value, Class targetClazz, Object methodName) {
            if (value instanceof List) {
                List values = (List) value;
                List retList = new ArrayList<>(values.size());
                for (final Object source : values) {
                    retList.add(BeanCopierUtil.convert(source, targetClazz));
                }
                return retList;
            } else if (value instanceof Map) {
                // TODO 暂时用不到，后续有需要再补充
            } else if (!ClassUtil.isPrimitive(targetClazz)) {
                return BeanCopierUtil.convert(value, targetClazz);
            }
            return value;
        }
    }

}
