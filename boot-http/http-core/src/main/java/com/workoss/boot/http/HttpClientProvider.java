package com.workoss.boot.http;

@FunctionalInterface
public interface HttpClientProvider {

	HttpClient createInstance();
}
