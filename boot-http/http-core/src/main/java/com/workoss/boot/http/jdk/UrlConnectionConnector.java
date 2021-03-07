package com.workoss.boot.http.jdk;

import com.workoss.boot.http.HttpClientConnector;
import com.workoss.boot.http.HttpClientRequest;
import com.workoss.boot.http.HttpClientResponse;

import java.util.function.Function;

public class UrlConnectionConnector implements HttpClientConnector {


	@Override
	public int order() {
		return 4;
	}

	@Override
	public HttpClientResponse connect(HttpClientRequest request, Function<? super HttpClientRequest, Void> requestCallback) {
		return null;
	}
}
