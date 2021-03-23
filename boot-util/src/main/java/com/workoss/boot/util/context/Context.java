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
package com.workoss.boot.util.context;

/**
 * 上下文
 *
 * @param <K> key
 * @param <V> value
 * @author workoss
 */
public interface Context<K, V> {

	/**
	 * 是否包含
	 * @param key key
	 * @return true/false
	 */
	boolean containsKey(K key);

	/**
	 * 获取值
	 * @param key key
	 * @return value
	 */
	V get(K key);

	/**
	 * 获取值/默认值
	 * @param key key
	 * @param defaultValue 默认值
	 * @return 值
	 */
	V get(K key, V defaultValue);

	/**
	 * set值
	 * @param key key
	 * @param value 值
	 */
	void set(K key, V value);

}
