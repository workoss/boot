/*
 * Copyright © 2020-2021 workoss (WORKOSS)
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
package com.workoss.boot.http;

import com.workoss.boot.annotation.lang.Nullable;
import com.workoss.boot.util.Assert;
import com.workoss.boot.util.LinkedMultiValueMap;
import com.workoss.boot.util.MultiValueMap;

import java.util.*;
import java.util.function.Consumer;

class DefaultHttpClientBuilder implements HttpClient.Builder {

	@Nullable
	private String baseUrl;

	@Nullable
	private HttpHeaders defaultHeaders;

	@Nullable
	private MultiValueMap<String, String> defaultCookies;

	private Consumer<HttpClient.RequestHeadersSpec<?>> defaultRequest;

	@Nullable
	private List<HttpClientFilter> filters;

	@Nullable
	private HttpClientHandler httpClientHandler;

	@Nullable
	private HttpClientConnector connector;

	public DefaultHttpClientBuilder() {
	}

	public DefaultHttpClientBuilder(DefaultHttpClientBuilder other) {
		Assert.notNull(other, "DefaultWebClientBuilder must not be null");
		this.baseUrl = other.baseUrl;
		if (other.defaultHeaders != null) {
			this.defaultHeaders = new HttpHeaders();
			this.defaultHeaders.putAll(other.defaultHeaders);
		}
		else {
			this.defaultHeaders = null;
		}
		this.defaultCookies = (other.defaultCookies != null ? new LinkedMultiValueMap<>(other.defaultCookies) : null);
		this.defaultRequest = other.defaultRequest;
		this.filters = (other.filters != null ? new ArrayList<>(other.filters) : null);
		this.connector = other.connector;
		this.httpClientHandler = other.httpClientHandler;
	}

	@Override
	public HttpClient.Builder baseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
		return this;
	}

	@Override
	public HttpClient.Builder defaultHeader(String header, String... values) {
		initHeaders().put(header, Arrays.asList(values));
		return this;
	}

	@Override
	public HttpClient.Builder defaultHeaders(Consumer<HttpHeaders> headersConsumer) {
		headersConsumer.accept(initHeaders());
		return null;
	}

	private HttpHeaders initHeaders() {
		if (this.defaultHeaders == null) {
			this.defaultHeaders = new HttpHeaders();
		}
		return this.defaultHeaders;
	}

	@Override
	public HttpClient.Builder defaultCookie(String cookie, String... values) {
		initCookies().addAll(cookie, Arrays.asList(values));
		return this;
	}

	@Override
	public HttpClient.Builder defaultCookies(Consumer<MultiValueMap<String, String>> cookiesConsumer) {
		cookiesConsumer.accept(initCookies());
		return this;
	}

	private MultiValueMap<String, String> initCookies() {
		if (this.defaultCookies == null) {
			this.defaultCookies = new LinkedMultiValueMap<>(3);
		}
		return this.defaultCookies;
	}

	@Override
	public HttpClient.Builder defaultRequest(Consumer<HttpClient.RequestHeadersSpec<?>> defaultRequest) {
		this.defaultRequest = this.defaultRequest != null ? this.defaultRequest.andThen(defaultRequest)
				: defaultRequest;
		return this;
	}

	@Override
	public HttpClient.Builder clientConnector(HttpClientConnector httpConnector) {
		this.connector = httpConnector;
		return this;
	}

	@Override
	public HttpClient.Builder filter(HttpClientFilter filter) {
		Assert.notNull(filter, "HttpClientFilter 不能为空");
		initFilters().add(filter);
		return this;
	}

	@Override
	public HttpClient.Builder filters(Consumer<List<HttpClientFilter>> filtersConsumer) {
		filtersConsumer.accept(initFilters());
		return this;
	}

	private List<HttpClientFilter> initFilters() {
		if (this.filters == null) {
			this.filters = new ArrayList<>();
		}
		return this.filters;
	}

	@Override
	public HttpClient.Builder clone() {
		return new DefaultHttpClientBuilder(this);
	}

	@Override
	public HttpClient build() {
		HttpClientConnector connectorToUse = (this.connector != null ? this.connector : initConnector());
		HttpClientHandler handler = (this.httpClientHandler == null ? HttpClientHandlers.create(connectorToUse)
				: this.httpClientHandler);
		HttpClientHandler httpClientHandler = (this.filters != null ? this.filters.stream()
				.reduce(HttpClientFilter::andThen).map(filter -> filter.apply(handler)).orElse(handler) : handler);
		HttpHeaders defaultHeaders = copyDefaultHeaders();
		MultiValueMap<String, String> defaultCookies = copyDefaultCookies();
		return new DefaultHttpClient(httpClientHandler, defaultHeaders, defaultCookies,
				new DefaultHttpClientBuilder(this));
	}

	private HttpClientConnector initConnector() {
		ServiceLoader<HttpClientConnector> connectorServiceLoader = ServiceLoader.load(HttpClientConnector.class);
		List<HttpClientConnector> httpClientConnectors = new ArrayList<>();
		connectorServiceLoader.forEach(loaderConnector -> {
			httpClientConnectors.add(loaderConnector);
		});
		if (httpClientConnectors.size() == 0) {
			throw new IllegalStateException("No suitable default ClientHttpConnector found");
		}
		httpClientConnectors.sort(Comparator.comparingInt(HttpClientConnector::order));
		return httpClientConnectors.get(0);
	}

	@Nullable
	private HttpHeaders copyDefaultHeaders() {
		if (this.defaultHeaders != null) {
			HttpHeaders copy = new HttpHeaders();
			this.defaultHeaders.forEach((key, values) -> copy.put(key, new ArrayList<>(values)));
			return copy;
		}
		else {
			return null;
		}
	}

	@Nullable
	private MultiValueMap<String, String> copyDefaultCookies() {
		if (this.defaultCookies != null) {
			MultiValueMap<String, String> copy = new LinkedMultiValueMap<>(this.defaultCookies.size());
			this.defaultCookies.forEach((key, values) -> copy.put(key, new ArrayList<>(values)));
			return this.defaultCookies;
		}
		else {
			return null;
		}
	}

}
