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

import com.workoss.boot.http.HttpClientResponse;
import com.workoss.boot.http.HttpHeaders;
import com.workoss.boot.http.HttpStatus;
import com.workoss.boot.util.LinkedMultiValueMap;
import com.workoss.boot.util.MultiValueMap;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Okhttp3ClientResponse implements HttpClientResponse {

	private static final Logger log = LoggerFactory.getLogger(Okhttp3ClientResponse.class);

	private final Response response;

	public Okhttp3ClientResponse(Response response) {
		this.response = response;
	}

	@Override
	public HttpStatus status() {
		return HttpStatus.resolve(response.code());
	}

	@Override
	public int rawStatusCode() {
		return response.code();
	}

	@Override
	public HttpHeaders headers() {
		return HttpHeaders.of(response.headers().toMultimap());
	}

	@Override
	public MultiValueMap<String, String> cookies() {
		List<String> cookies = response.headers().values("Set-Cookie");
		MultiValueMap multiValueMap = new LinkedMultiValueMap();
		if (cookies == null) {
			return new LinkedMultiValueMap<>();
		}
		cookies.stream().forEach(string -> {
			Arrays.stream(string.split(";")).forEach(keyvalue -> {
				String[] sz = keyvalue.trim().split("=");
				multiValueMap.add(sz[0], sz[1]);
			});
		});
		return multiValueMap;
	}

	@Override
	public ByteBuffer byteBody() {
		return checkBody(response1 -> {
			try {
				return ByteBuffer.wrap(response1.body().bytes());
			}
			catch (IOException e) {
				log.error("[OKHTTP] stringBody error", e);
			}
			return null;
		});
	}

	@Override
	public InputStream body() {
		return checkBody(response1 -> response1.body().byteStream());
	}

	@Override
	public String stringBody() {
		return checkBody(response1 -> {
			try {
				return response1.body().string();
			}
			catch (IOException e) {
				log.error("[OKHTTP] stringBody error", e);
			}
			return null;
		});
	}

	private <T> T checkBody(Function<Response, T> bodyFunc) {
		long byteCount = response.headers().byteCount();
		if (byteCount <= 0) {
			return null;
		}

		return bodyFunc.apply(response);
	}

	@Override
	public void close() throws IOException {
		if (response != null) {
			response.close();
		}
	}

}
