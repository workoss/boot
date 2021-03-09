package com.workoss.boot.storage.context;

import java.util.HashMap;

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
