package com.workoss.boot.plugin.mybatis.example;

import com.workoss.boot.util.reflect.ClassUtils;

public interface Example<T> {

	static <T> Example<T> of(T probe) {
		return new TypedExample<>(probe, ExampleMatcher.matching());
	}

	static <T> Example<T> of(T probe, ExampleMatcher matcher) {
		return new TypedExample<>(probe, matcher);
	}

	T getProbe();

	ExampleMatcher getMatcher();


	default Class<T> getProbeType() {
		//TODO
		return (Class<T>) ClassUtils.unwrapCglib(getProbe().getClass());
	}

}
