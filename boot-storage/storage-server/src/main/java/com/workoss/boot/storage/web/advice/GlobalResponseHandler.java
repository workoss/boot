/*
 * Copyright 2019-2022 workoss (https://www.workoss.com)
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
package com.workoss.boot.storage.web.advice;

import com.workoss.boot.util.DateUtils;
import com.workoss.boot.util.model.ResultInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * 全局返回处理器
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
@Slf4j
public class GlobalResponseHandler extends ResponseBodyResultHandler {

	private static MethodParameter METHOD_PARAMETER_MONO_COMMON_RESULT;

	public GlobalResponseHandler(List<HttpMessageWriter<?>> writers, RequestedContentTypeResolver resolver) {
		super(writers, resolver);
		this.setOrder(99);
		initMethodParameter();
	}

	public GlobalResponseHandler(List<HttpMessageWriter<?>> writers, RequestedContentTypeResolver resolver,
			ReactiveAdapterRegistry registry) {
		super(writers, resolver, registry);
		initMethodParameter();
	}

	@Override
	public boolean supports(HandlerResult result) {
		MethodParameter returnType = result.getReturnTypeSource();
		Class<?> containingClass = returnType.getContainingClass();
		return (AnnotatedElementUtils.hasAnnotation(containingClass, ResponseBody.class)
				|| returnType.hasMethodAnnotation(ResponseBody.class));
	}

	@Override
	public Mono<Void> handleResult(ServerWebExchange exchange, HandlerResult result) {
		Object returnValue = result.getReturnValue();
		ServerHttpResponse response = exchange.getResponse();
		response.beforeCommit(() -> {
			response.getHeaders().add(HttpHeaders.DATE, DateUtils.getCurrentDateTime("yyyy-MM-dd HH:mm:ss.SSS"));
			return Mono.empty();
		});
		HttpStatus responseStatus = Optional.of(exchange.getResponse())
			.map(ServerHttpResponse::getStatusCode)
			.orElse(HttpStatus.NOT_FOUND);
		if (HttpStatus.OK.compareTo(responseStatus) != 0) {
			return writeBody(result.getReturnValue(), result.getReturnTypeSource(), exchange);
		}
		Object body;
		// 处理返回结果为 Mono 的情况
		if (returnValue instanceof Mono) {
			body = ((Mono<Object>) returnValue).map((Function<Object, Object>) this::wrapResultInfo)
				.defaultIfEmpty(ResultInfo.success());
			// 处理返回结果为 Flux 的情况
		}
		else if (returnValue instanceof Flux) {
			body = ((Flux<Object>) returnValue).collectList()
				.map((Function<Object, Object>) this::wrapResultInfo)
				.defaultIfEmpty(ResultInfo.success());
			// 处理结果为其它类型
		}
		else {
			body = wrapResultInfo(returnValue);
		}
		return writeBody(body, METHOD_PARAMETER_MONO_COMMON_RESULT, exchange);
	}

	private static Mono<ResultInfo> methodForParams() {
		return null;
	}

	private void initMethodParameter() {
		if (METHOD_PARAMETER_MONO_COMMON_RESULT != null) {
			return;
		}
		try {
			// 获得 METHOD_PARAMETER_MONO_COMMON_RESULT 。其中 -1 表示 `#methodForParams()`
			// 方法的返回值
			METHOD_PARAMETER_MONO_COMMON_RESULT = new MethodParameter(
					GlobalResponseHandler.class.getDeclaredMethod("methodForParams"), -1);
		}
		catch (NoSuchMethodException e) {
			log.error("[GLOBAL][获取 METHOD_PARAMETER_MONO_COMMON_RESULT 时，找不都方法");
			throw new RuntimeException(e);
		}
	}

	private ResultInfo wrapResultInfo(Object body) {
		if (body instanceof ResultInfo) {
			return (ResultInfo) body;
		}
		return ResultInfo.success(body);
	}

}
