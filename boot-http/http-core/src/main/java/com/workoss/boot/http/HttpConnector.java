package com.workoss.boot.http;

import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

public interface HttpConnector {

	Mono<HttpResponse> connect(HttpMethod method, URI uri, Function<? super HttpRequest, Mono<Void>> requestCallback);

}
