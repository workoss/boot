package com.workoss.boot.util;

import java.util.Map;

@FunctionalInterface
public interface ApplyClassFunc<T> {

    Class<T> apply(Map<String, String> stringStringMap);
}
