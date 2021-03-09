package com.workoss.boot.storage.context;

public interface Context<K, V> {

	boolean containsKey(K key);

	V get(K key);

	V get(K key, V defaultValue);

	void set(K key, V value);

}
