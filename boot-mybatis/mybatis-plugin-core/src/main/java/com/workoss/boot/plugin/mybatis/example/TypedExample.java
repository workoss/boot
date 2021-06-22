/*
 * Copyright 2019-2021 workoss (https://www.workoss.com)
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
