package com.workoss.boot.plugin.mybatis.example;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

class TypedExample<T> implements Example<T> {

	private final T probe;
	private final ExampleMatcher matcher;

	TypedExample(T probe, ExampleMatcher matcher) {

		Assert.notNull(probe, "Probe must not be null");
		Assert.notNull(matcher, "ExampleMatcher must not be null");

		this.probe = probe;
		this.matcher = matcher;
	}

	public T getProbe() {
		return this.probe;
	}

	public ExampleMatcher getMatcher() {
		return this.matcher;
	}


	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}

		if (!(o instanceof TypedExample)) {
			return false;
		}

		TypedExample<?> that = (TypedExample<?>) o;
		if (!ObjectUtils.nullSafeEquals(probe, that.probe)) {
			return false;
		}

		return ObjectUtils.nullSafeEquals(matcher, that.matcher);
	}


	@Override
	public int hashCode() {
		int result = ObjectUtils.nullSafeHashCode(probe);
		result = 31 * result + ObjectUtils.nullSafeHashCode(matcher);
		return result;
	}


	@Override
	public String toString() {
		return "TypedExample{" + "probe=" + probe + ", matcher=" + matcher + '}';
	}
}
