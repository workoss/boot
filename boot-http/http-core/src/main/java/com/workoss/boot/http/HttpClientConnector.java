package com.workoss.boot.http;

import java.util.function.Function;

public interface HttpClientConnector {

	int order();

	HttpClientResponse connect(HttpClientRequest request, Function<? super HttpClientRequest, Void> requestCallback);

}
