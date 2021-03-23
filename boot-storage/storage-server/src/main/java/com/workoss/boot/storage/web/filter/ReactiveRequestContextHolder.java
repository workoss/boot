/*
 * Copyright 2019-2021 workoss (https://www.workoss.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.workoss.boot.storage.web.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

/**
 * ReactiveRequestContextHolder
 *
 * @author workoss
 */
public class ReactiveRequestContextHolder {

	static final Class<ServerHttpRequest> CONTEXT_KEY = ServerHttpRequest.class;

	public static Mono<ServerHttpRequest> getRequest() {
		return Mono.deferContextual(Mono::just).map(context -> context.get(CONTEXT_KEY));
	}

}
