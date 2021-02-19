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
	 * -> single value map
	 * @return map
	 */
	Map<K, V> toSingleValueMap();

}
