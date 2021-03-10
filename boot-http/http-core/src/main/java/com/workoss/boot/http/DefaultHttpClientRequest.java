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
import com.workoss.boot.util.MultiValueMap;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.function.Consumer;

public class DefaultHttpClientRequest implements HttpClientRequest {

	private final HttpMethod method;

	private final URI url;

	private final HttpHeaders headers;

	private final MultiValueMap<String, String> cookies;

	private final Map<String, Object> attributes;

	private final ByteBuffer body;

	@Nullable
	private final Consumer<HttpClientRequest> requestConsumer;

	public DefaultHttpClientRequest(HttpMethod method, URI url, HttpHeaders headers,
			MultiValueMap<String, String> cookies, Map<String, Object> attributes, ByteBuffer body,
			@Nullable Consumer<HttpClientRequest> requestConsumer) {
		this.method = method;
		this.url = url;
		this.headers = headers;
		this.cookies = cookies;
		this.attributes = attributes;
		this.body = body;
		this.requestConsumer = requestConsumer;
	}

	@Override
	public HttpMethod method() {
		return this.method;
	}

	@Override
	public URI url() {
		return this.url;
	}

	@Override
	public HttpHeaders headers() {
		return this.headers;
	}

	@Override
	public MultiValueMap<String, String> cookies() {
		return this.cookies;
	}

	@Override
	public Map<String, Object> attributes() {
		return this.attributes;
	}

	@Override
	public Consumer<HttpClientRequest> httpRequest() {
		return this.requestConsumer;
	}

	@Override
	public ByteBuffer body() {
		return this.body;
	}

}
