package com.workoss.boot.util.xml;

import java.util.Map;

@FunctionalInterface
public interface XmlToClassFunction<T> {

    Class<T> apply(Map<String, String> stringStringMap);
}
