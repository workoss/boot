package com.workoss.boot.http;

@FunctionalInterface
public interface HttpClientHandler {

	HttpClientResponse handler(HttpClientRequest request);

	default HttpClientHandler filter(HttpClientFilter filter) {
		return filter.apply(this);
	}

}
