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

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

final class DefaultHttpClientRequestBuilder implements HttpClientRequest.Builder {

	private HttpMethod method;

	private URI url;

	private final HttpHeaders headers = new HttpHeaders();

	private final MultiValueMap<String, String> cookies = new LinkedMultiValueMap<>();

	private final Map<String, Object> attributes = new LinkedHashMap<>();

	private ByteBuffer body;

	@Nullable
	private Consumer<HttpClientRequest> requestConsumer;

	public DefaultHttpClientRequestBuilder(HttpMethod method, URI url) {
		this.method = method;
		this.url = url;
	}

	@Override
	public HttpClientRequest.Builder method(HttpMethod method) {
		Assert.notNull(method, "HttpMethod 不能为空");
		this.method = method;
		return this;
	}

	@Override
	public HttpClientRequest.Builder url(URI url) {
		Assert.notNull(url, "URI 不能为空");
		this.url = url;
		return this;
	}

	@Override
	public HttpClientRequest.Builder header(String headerName, String... values) {
		for (String value : values) {
			this.headers.add(headerName, value);
		}
		return this;
	}

	@Override
	public HttpClientRequest.Builder headers(Consumer<HttpHeaders> headersConsumer) {
		headersConsumer.accept(this.headers);
		return this;
	}

	@Override
	public HttpClientRequest.Builder cookie(String cookie, String... values) {
		for (String value : values) {
			this.headers.add(cookie, value);
		}
		return this;
	}

	@Override
	public HttpClientRequest.Builder cookies(Consumer<MultiValueMap<String, String>> cookiesConsumer) {
		cookiesConsumer.accept(this.cookies);
		return this;
	}

	@Override
	public HttpClientRequest.Builder attribute(String name, Object value) {
		this.attributes.put(name, value);
		return this;
	}

	@Override
	public HttpClientRequest.Builder attributes(Consumer<Map<String, Object>> attributesConsumer) {
		attributesConsumer.accept(this.attributes);
		return this;
	}

	@Override
	public HttpClientRequest.Builder httpRequest(Consumer<HttpClientRequest> requestConsumer) {
		this.requestConsumer = (this.requestConsumer != null ? this.requestConsumer.andThen(requestConsumer)
				: requestConsumer);
		return this;
	}

	@Override
	public HttpClientRequest.Builder body(ByteBuffer byteBuffer) {
		this.body = byteBuffer;
		return this;
	}

	@Override
	public HttpClientRequest build() {
		return new DefaultHttpClientRequest(this.method, this.url, this.headers, this.cookies, this.attributes,
				this.body, this.requestConsumer);
	}

}
