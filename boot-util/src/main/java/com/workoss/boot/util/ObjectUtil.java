package com.workoss.boot.util;

public class ObjectUtil {
    public static <T> T checkNotNull(T arg, String text){
        if (arg == null) {
            throw new NullPointerException(text);
        }
        return arg;
    }
}
