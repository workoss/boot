package com.workoss.boot.http.netty;

import com.workoss.boot.http.HttpConnector;
import com.workoss.boot.http.HttpMethod;
import com.workoss.boot.http.HttpRequest;
import com.workoss.boot.http.HttpResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

public class ReactorNettyConnector implements HttpConnector {
	@Override
	public Mono<HttpResponse> connect(HttpMethod method, URI uri, Function<? super HttpRequest, Mono<Void>> requestCallback) {
		return null;
	}
}
