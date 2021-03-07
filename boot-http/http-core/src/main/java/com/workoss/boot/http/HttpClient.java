package com.workoss.boot.http;

import com.workoss.boot.util.MultiValueMap;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public interface HttpClient {

	RequestBodyUriSpec method(HttpMethod method);

	Builder mutate();

	static HttpClient create(String baseUrl) {
		return new DefaultHttpClientBuilder().baseUrl(baseUrl).build();
	}

	static Builder builder() {
		return new DefaultHttpClientBuilder();
	}

	interface Builder {

		Builder baseUrl(String baseUrl);

		Builder defaultHeader(String header, String... values);

		Builder defaultHeaders(Consumer<HttpHeaders> headersConsumer);

		Builder defaultCookie(String cookie, String... values);

		Builder defaultCookies(Consumer<MultiValueMap<String, String>> cookiesConsumer);

		Builder defaultRequest(Consumer<RequestHeadersSpec<?>> defaultRequest);

		Builder clientConnector(HttpClientConnector httpConnector);

		Builder filter(HttpClientFilter filter);

		Builder filters(Consumer<List<HttpClientFilter>> filtersConsumer);

		Builder clone();

		HttpClient build();

	}

	interface UriSpec<S extends RequestHeadersSpec<?>> {

		S uri(URI uri);

		S uri(String uri, Map<String, String> uriVariables);

		S uri(String uri, Function<String, String> uriFunc);

	}

	interface RequestBodySpec extends RequestHeadersSpec<RequestBodySpec> {

		RequestBodySpec contentLength(long contentLength);

		RequestBodySpec contentType(MediaType contentType);

		RequestHeadersSpec<?> bodyValue(Object body);

	}

	interface RequestHeadersSpec<S extends RequestHeadersSpec<S>> {

		S cookie(String name, String value);

		S cookies(Consumer<MultiValueMap<String, String>> cookiesConsumer);

		S header(String headerName, String... headerValues);

		S headers(Consumer<HttpHeaders> headersConsumer);

		S attribute(String name, Object value);

		S attributes(Consumer<Map<String, Object>> attributesConsumer);

		S httpRequest(Consumer<HttpClientRequest> requestConsumer);

		HttpClientResponse execute();

	}

	interface RequestHeadersUriSpec<S extends RequestHeadersSpec<S>> extends UriSpec<S>, RequestHeadersSpec<S> {

	}

	interface RequestBodyUriSpec extends RequestBodySpec, RequestHeadersUriSpec<RequestBodySpec> {

	}

}
