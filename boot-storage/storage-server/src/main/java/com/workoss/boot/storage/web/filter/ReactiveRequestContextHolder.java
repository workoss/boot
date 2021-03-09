package com.workoss.boot.storage.web.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

public class ReactiveRequestContextHolder {

	static final Class<ServerHttpRequest> CONTEXT_KEY = ServerHttpRequest.class;

	public static Mono<ServerHttpRequest> getRequest() {
		return Mono.deferContextual(Mono::just).map(context -> context.get(CONTEXT_KEY));
	}

}
