package com.workoss.boot.http;

import com.workoss.boot.annotation.lang.Nullable;
import com.workoss.boot.http.util.HttpCommonUtil;
import com.workoss.boot.util.LinkedMultiValueMap;
import com.workoss.boot.util.MultiValueMap;
import com.workoss.boot.util.collection.CollectionUtils;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

class DefaultHttpClient implements HttpClient {

	private final HttpClientHandler httpClientHandler;

	private final HttpHeaders defaultHeaders;

	private final MultiValueMap<String, String> defaultCookies;

	private final DefaultHttpClientBuilder builder;

	DefaultHttpClient(HttpClientHandler httpClientHandler, HttpHeaders defaultHeaders,
			MultiValueMap<String, String> defaultCookies, DefaultHttpClientBuilder builder) {
		this.httpClientHandler = httpClientHandler;
		this.defaultHeaders = defaultHeaders;
		this.defaultCookies = defaultCookies;
		this.builder = builder;
	}

	@Override
	public RequestBodyUriSpec method(HttpMethod method) {
		return methodInternal(method);
	}

	@Override
	public Builder mutate() {
		return new DefaultHttpClientBuilder(this.builder);
	}

	private RequestBodyUriSpec methodInternal(HttpMethod httpMethod) {
		return new DefaultRequestBodyUriSpec(httpMethod);
	}

	private class DefaultRequestBodyUriSpec implements RequestBodyUriSpec {

		private final HttpMethod httpMethod;

		@Nullable
		private URI uri;

		@Nullable
		private HttpHeaders headers;

		@Nullable
		private MultiValueMap<String, String> cookies;

		private final Map<String, Object> attributes = new LinkedHashMap<>(4);

		@Nullable
		private Consumer<HttpClientRequest> httpRequestConsumer;

		public DefaultRequestBodyUriSpec(HttpMethod httpMethod) {
			this.httpMethod = httpMethod;
		}

		@Override
		public RequestBodyUriSpec contentLength(long contentLength) {
			getHeaders().setContentLength(contentLength);
			return this;
		}

		@Override
		public RequestBodyUriSpec contentType(MediaType contentType) {
			getHeaders().setContentType(contentType);
			return this;
		}

		@Override
		public RequestBodyUriSpec bodyValue(Object body) {
			return null;
		}

		@Override
		public RequestBodyUriSpec cookie(String name, String value) {
			getCookies().add(name, value);
			return this;
		}

		@Override
		public RequestBodyUriSpec cookies(Consumer cookiesConsumer) {
			cookiesConsumer.accept(getCookies());
			return this;
		}

		@Override
		public RequestBodyUriSpec header(String headerName, String... headerValues) {
			for (String headerValue : headerValues) {
				getHeaders().add(headerName, headerValue);
			}
			return this;
		}

		@Override
		public RequestBodyUriSpec headers(Consumer headersConsumer) {
			headersConsumer.accept(getHeaders());
			return this;
		}

		@Override
		public RequestBodyUriSpec attribute(String name, Object value) {
			this.attributes.put(name, value);
			return this;
		}

		@Override
		public RequestBodyUriSpec attributes(Consumer attributesConsumer) {
			attributesConsumer.accept(this.attributes);
			return this;
		}

		@Override
		public RequestBodyUriSpec uri(URI uri) {
			this.uri = uri;
			return this;
		}

		@Override
		public RequestBodyUriSpec uri(String uri, Map<String, String> uriVariables) {
			this.uri = URI.create(HttpCommonUtil.renderUrl(uri,uriVariables));
			return this;
		}

		@Override
		public RequestBodyUriSpec uri(String uri, Function<String, String> uriFunc) {
			this.uri = URI.create(uriFunc.apply(uri));
			return this;
		}

		@Override
		public RequestBodySpec httpRequest(Consumer<HttpClientRequest> requestConsumer) {
			this.httpRequestConsumer = (this.httpRequestConsumer != null
					? this.httpRequestConsumer.andThen(requestConsumer) : requestConsumer);
			return this;
		}

		@Override
		public HttpClientResponse execute() {
			return httpClientHandler.handler(initRequestBuilder().build());
		}

		private HttpClientRequest.Builder initRequestBuilder() {
			HttpClientRequest.Builder builder = HttpClientRequest.create(this.httpMethod, initUrl())
					.headers(headers -> headers.addAll(initHeaders())).cookies(cookies -> cookies.addAll(initCookies()))
					.attributes(attributes -> attributes.putAll(this.attributes));
			if (this.httpRequestConsumer != null) {
				builder.httpRequest(this.httpRequestConsumer);
			}
			return builder;
		}

		private HttpHeaders initHeaders() {
			if (CollectionUtils.isEmpty(this.headers)) {
				return (defaultHeaders != null ? defaultHeaders : new HttpHeaders());
			}
			else if (CollectionUtils.isEmpty(defaultHeaders)) {
				return this.headers;
			}
			else {
				HttpHeaders result = new HttpHeaders();
				result.putAll(defaultHeaders);
				result.putAll(this.headers);
				return result;
			}
		}

		// TODO 格式化url
		private URI initUrl() {
			return uri != null ? this.uri : null;
		}

		private MultiValueMap<String, String> initCookies() {
			if (CollectionUtils.isEmpty(this.cookies)) {
				return (defaultCookies != null ? defaultCookies : new LinkedMultiValueMap<>());
			}
			else if (CollectionUtils.isEmpty(defaultCookies)) {
				return this.cookies;
			}
			else {
				MultiValueMap<String, String> result = new LinkedMultiValueMap<>();
				result.putAll(defaultCookies);
				result.putAll(this.cookies);
				return result;
			}
		}

		private HttpHeaders getHeaders() {
			if (this.headers == null) {
				this.headers = new HttpHeaders();
			}
			return this.headers;
		}

		private MultiValueMap<String, String> getCookies() {
			if (this.cookies == null) {
				this.cookies = new LinkedMultiValueMap<>(3);
			}
			return this.cookies;
		}

	}

}
