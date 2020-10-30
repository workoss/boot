package com.workoss.boot.http;

import java.util.Iterator;
import java.util.ServiceLoader;

public final class HttpClientProviders {

	private static HttpClientProvider defaultProvider;

	static {
		ServiceLoader<HttpClientProvider> serviceLoader = ServiceLoader.load(HttpClientProvider.class);
		// Use the first provider found in the service loader iterator.
		Iterator<HttpClientProvider> it = serviceLoader.iterator();
		if (it.hasNext()) {
			defaultProvider = it.next();
		}
	}

	private HttpClientProviders() {
	}

	public static HttpClient createInstance() {
		if (defaultProvider == null) {
			throw new IllegalStateException("have no SPI on the classpath");
		}
		return defaultProvider.createInstance();
	}

}
