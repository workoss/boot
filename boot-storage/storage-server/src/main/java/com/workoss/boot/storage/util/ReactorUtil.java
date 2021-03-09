package com.workoss.boot.storage.util;

import com.github.benmanes.caffeine.cache.Cache;
import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ReactorUtil {

	private static <K, V> Function<K, Mono<Signal<? extends V>>> reader(Cache<K, Signal<? extends V>> cache) {
		return key -> {
			return Mono.justOrEmpty(cache.getIfPresent(key));
		};
	}

	private static <K, V> BiFunction<K, Signal<? extends V>, Mono<Void>> writer(Cache<K, Signal<? extends V>> cache) {
		return (key, value) -> {
			return Mono.fromRunnable(() -> cache.put(key, value));
		};
	}

	public static <K, V> Mono<V> createCacheMono(Cache<K, Signal<? extends V>> cache, K key, Mono<V> defaultValue) {
		return CacheMono.lookup(ReactorUtil.reader(cache), key).onCacheMissResume(defaultValue)
				.andWriteWith(ReactorUtil.writer(cache));
	}

	public static <K, V> Mono<V> createCacheMono(Cache<K, Signal<? extends V>> cache, K key,
			Function<K, Mono<V>> valueConvert) {
		return CacheMono.lookup(ReactorUtil.reader(cache), key).onCacheMissResume(valueConvert.apply(key))
				.andWriteWith(ReactorUtil.writer(cache));
	}

}
