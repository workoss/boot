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
package com.workoss.boot.util;

import com.workoss.boot.annotation.lang.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Extension of the {@code Map} interface that stores multiple values.
 *
 * @param <K> key
 * @param <V> value
 * @author workoss
 */
@SuppressWarnings("unused")
public interface MultiValueMap<K, V> extends Map<K, List<V>> {

	/**
	 * 返回 first value
	 * @param key key
	 * @return value
	 */
	@Nullable
	V getFirst(K key);

	/**
	 * add key:value to list
	 * @param key key
	 * @param value value
	 */
	void add(K key, @Nullable V value);

	/**
	 * add listvalues
	 * @param key key
	 * @param values values
	 */
	void addAll(K key, List<? extends V> values);

	/**
	 * add multiValueMap
	 * @param values multiValueMap
	 */
	void addAll(MultiValueMap<K, V> values);

	/**
	 * key not exists add
	 * @param key key
	 * @param value value
	 */
	default void addIfAbsent(K key, @Nullable V value) {
		if (!containsKey(key)) {
			add(key, value);
		}
	}

	/**
	 * set key:value
	 * @param key key
	 * @param value value
	 */
	void set(K key, @Nullable V value);

	/**
	 * set map
	 * @param values map
	 */
	void setAll(Map<K, V> values);

	/**
	 * single value map
	 * @return map
	 */
	Map<K, V> toSingleValueMap();

}
