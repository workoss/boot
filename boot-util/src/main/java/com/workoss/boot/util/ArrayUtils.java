package com.workoss.boot.util;

/**
 * 集合工具类
 */
public abstract class ArrayUtils {

	public static String[] concat(String[] a, String[] b) {
		if (b == null) {
			return a;
		}
		if (a == null) {
			return b;
		}
		String[] c = new String[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

}
