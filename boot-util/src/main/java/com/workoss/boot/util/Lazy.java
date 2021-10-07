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
package com.workoss.boot.util;

import com.workoss.boot.annotation.lang.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 懒加载
 *
 * @param <T> 泛型
 * @author workoss
 */
@SuppressWarnings("unused")
public class Lazy<T> implements Supplier<T> {

	private static final Lazy<?> EMPTY = new Lazy<>(() -> null, null, true);

	private final Supplier<? extends T> supplier;

	private @Nullable T value;

	private volatile boolean resolved;

	private Lazy(Supplier<? extends T> supplier) {
		this(supplier, null, false);
	}

	private Lazy(Supplier<? extends T> supplier, @Nullable T value, boolean resolved) {
		this.supplier = supplier;
		this.value = value;
		this.resolved = resolved;
	}

	public static <T> Lazy<T> of(Supplier<? extends T> supplier) {
		return new Lazy<>(supplier);
	}

	public static <T> Lazy<T> of(T value) {
		Assert.notNull(value, "Value must not be null!");
		return new Lazy<>(() -> value);
	}

	@Override
	public T get() {
		T value = getNullable();

		if (value == null) {
			throw new IllegalStateException("Expected lazy evaluation to yield a non-null value but got null!");
		}

		return value;
	}

	public Optional<T> getOptional() {
		return Optional.ofNullable(getNullable());
	}

	public Lazy<T> or(Supplier<? extends T> supplier) {

		Assert.notNull(supplier, "Supplier must not be null!");

		return Lazy.of(() -> orElseGet(supplier));
	}

	public Lazy<T> or(T value) {

		Assert.notNull(value, "Value must not be null!");

		return Lazy.of(() -> orElse(value));
	}

	@Nullable
	public T orElse(@Nullable T value) {

		T nullable = getNullable();

		return nullable == null ? value : nullable;
	}

	@Nullable
	private T orElseGet(Supplier<? extends T> supplier) {

		Assert.notNull(supplier, "Default value supplier must not be null!");

		T value = getNullable();

		return value == null ? supplier.get() : value;
	}

	public <S> Lazy<S> map(Function<? super T, ? extends S> function) {

		Assert.notNull(function, "Function must not be null!");

		return Lazy.of(() -> function.apply(get()));
	}

	public <S> Lazy<S> flatMap(Function<? super T, Lazy<? extends S>> function) {

		Assert.notNull(function, "Function must not be null!");

		return Lazy.of(() -> function.apply(get()).get());
	}

	@Nullable
	public T getNullable() {

		if (resolved) {
			return value;
		}

		this.value = supplier.get();
		this.resolved = true;

		return value;
	}

	@Override
	public boolean equals(@Nullable Object o) {

		if (this == o) {
			return true;
		}

		if (!(o instanceof Lazy)) {
			return false;
		}

		Lazy<?> lazy = (Lazy<?>) o;

		if (resolved != lazy.resolved) {
			return false;
		}

		if (!ObjectUtil.nullSafeEquals(supplier, lazy.supplier)) {
			return false;
		}

		return ObjectUtil.nullSafeEquals(value, lazy.value);
	}

	@Override
	public int hashCode() {

		int result = ObjectUtil.nullSafeHashCode(supplier);

		result = 31 * result + ObjectUtil.nullSafeHashCode(value);
		result = 31 * result + (resolved ? 1 : 0);

		return result;
	}

}
