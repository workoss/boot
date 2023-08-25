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
package com.workoss.boot.util.context;

import java.util.HashMap;

/**
 * map 上下文
 *
 * @param <K> key
 * @param <V> value
 * @author workoss
 */
public class MapContext<K, V> extends HashMap<K, V> implements Context<K, V> {

	public static MapContext<String, String> EMPTY = new MapContext<>();

	@Override
	public V get(Object key) {
		return super.get(key);
	}

	@Override
	public V get(K key, V defaultValue) {
		return containsKey(key) ? get(key) : defaultValue;
	}

	@Override
	public void set(K key, V value) {
		this.put(key, value);
	}

}
