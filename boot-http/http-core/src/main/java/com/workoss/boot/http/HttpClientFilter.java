package com.workoss.boot.http;

import com.workoss.boot.util.Assert;

@FunctionalInterface
public interface HttpClientFilter {

	HttpClientResponse filter(HttpClientRequest request, HttpClientHandler nextHandler);

	default HttpClientFilter andThen(HttpClientFilter afterFilter) {
		Assert.notNull(afterFilter, "afterFilter 不能为空");
		return (request, next) -> filter(request, afterRequest -> afterFilter.filter(afterRequest, next));
	}

	default HttpClientHandler apply(HttpClientHandler handler) {
		Assert.notNull(handler, "HttpClientHandler 不能为空");
		return request -> this.filter(request, handler);
	}

}
