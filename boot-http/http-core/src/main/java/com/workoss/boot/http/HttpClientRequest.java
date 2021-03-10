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

import com.workoss.boot.util.MultiValueMap;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public interface HttpClientRequest {

	HttpMethod method();

	URI url();

	HttpHeaders headers();

	MultiValueMap<String, String> cookies();

	default Optional<Object> attribute(String name) {
		return Optional.ofNullable(attributes().get(name));
	}

	Map<String, Object> attributes();

	Consumer<HttpClientRequest> httpRequest();

	ByteBuffer body();

	static Builder create(HttpMethod method, URI url) {
		return new DefaultHttpClientRequestBuilder(method, url);
	}

	interface Builder {

		Builder method(HttpMethod method);

		Builder url(URI url);

		Builder header(String headerName, String... values);

		Builder headers(Consumer<HttpHeaders> headersConsumer);

		Builder cookie(String cookie, String... values);

		Builder cookies(Consumer<MultiValueMap<String, String>> cookiesConsumer);

		Builder attribute(String name, Object value);

		Builder attributes(Consumer<Map<String, Object>> attributesConsumer);

		Builder httpRequest(Consumer<HttpClientRequest> requestConsumer);

		Builder body(ByteBuffer byteBuffer);

		HttpClientRequest build();

	}

}
