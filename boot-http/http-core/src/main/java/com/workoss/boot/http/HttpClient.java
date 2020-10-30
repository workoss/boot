package com.workoss.boot.http;

import reactor.core.publisher.Mono;

public interface HttpClient {

	Mono<HttpResponse> send(HttpRequest request);

	static HttpClient create() {
		return null;
	}

}
