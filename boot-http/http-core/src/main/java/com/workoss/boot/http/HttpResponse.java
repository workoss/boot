package com.workoss.boot.http;

import com.workoss.boot.http.response.BufferedHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public abstract class HttpResponse implements Closeable {

	private HttpRequest request;

	protected HttpResponse(HttpRequest request) {
		this.request = request;
	}

	public abstract int getStatusCode();

	public abstract String getHeaderValue(String name);

	public abstract HttpHeaders getHeaders();

	public abstract Flux<ByteBuffer> getBody();

	public abstract Mono<byte[]> getBodyAsByteArray();

	public abstract Mono<String> getBodyAsString();

	public abstract Mono<String> getBodyAsString(Charset charset);

	public HttpRequest getRequest() {
		return request;
	}

	public HttpResponse buffer() {
		return new BufferedHttpResponse(this);
	}

	@Override
	public void close() throws IOException {

	}

}
