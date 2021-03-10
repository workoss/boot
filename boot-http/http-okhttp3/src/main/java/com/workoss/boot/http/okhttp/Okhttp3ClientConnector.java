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
package com.workoss.boot.http.okhttp;

import com.workoss.boot.http.HttpClientConnector;
import com.workoss.boot.http.HttpClientRequest;
import com.workoss.boot.http.HttpClientResponse;
import okhttp3.*;
import okio.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Function;

public class Okhttp3ClientConnector implements HttpClientConnector {

	private static final Logger log = LoggerFactory.getLogger(Okhttp3ClientConnector.class);

	private final OkHttpClient client;

	public Okhttp3ClientConnector() {
		this.client = new OkHttpClient.Builder().build();
	}

	@Override
	public int order() {
		return 3;
	}

	@Override
	public HttpClientResponse connect(HttpClientRequest request,
			Function<? super HttpClientRequest, Void> requestCallback) {
		log.info("[OKHTTP3] URI:{} method:{}", request.url(), request.method());

		RequestBody body = null;
		if (request.body() != null) {
			body = RequestBody.create(MediaType.parse(request.headers().getContentType().toString()),
					ByteString.of(request.body()));
		}
		Request okhttpRequest = new Request.Builder().url(request.url().toString())
				.method(request.method().name(), body).headers(Headers.of(request.headers().toSingleValueMap()))
				.build();
		try {
			requestCallback.apply(request);
			Response response = client.newCall(okhttpRequest).execute();
			return new Okhttp3ClientResponse(response);
		}
		catch (IOException e) {

		}
		return null;
	}

}
