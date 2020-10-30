package com.workoss.boot.http.response;

import com.workoss.boot.http.HttpHeaders;
import com.workoss.boot.http.HttpResponse;
import com.workoss.boot.http.util.FluxUtil;
import com.workoss.boot.http.util.HttpToolUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public final class BufferedHttpResponse extends HttpResponse {

	private final HttpResponse innerHttpResponse;

	private final Flux<ByteBuffer> cachedBody;

	public BufferedHttpResponse(HttpResponse innerHttpResponse) {
		super(innerHttpResponse.getRequest());
		this.innerHttpResponse = innerHttpResponse;
		this.cachedBody = FluxUtil.collectBytesInByteBufferStream(innerHttpResponse.getBody()).map(ByteBuffer::wrap)
				.flux().cache();
	}

	@Override
	public int getStatusCode() {
		return innerHttpResponse.getStatusCode();
	}

	@Override
	public String getHeaderValue(String name) {
		return innerHttpResponse.getHeaderValue(name);
	}

	@Override
	public HttpHeaders getHeaders() {
		return innerHttpResponse.getHeaders();
	}

	@Override
	public Flux<ByteBuffer> getBody() {
		return cachedBody;
	}

	@Override
	public Mono<byte[]> getBodyAsByteArray() {
		return cachedBody.next().map(ByteBuffer::array);
	}

	@Override
	public Mono<String> getBodyAsString() {
		return getBodyAsByteArray().map(bytes -> HttpToolUtil.bomAwareToString(bytes, HttpHeaders.CONTENT_TYPE));
	}

	@Override
	public Mono<String> getBodyAsString(Charset charset) {
		return getBodyAsByteArray().map(bytes -> bytes == null ? null : new String(bytes, charset));
	}

	@Override
	public HttpResponse buffer() {
		return this;
	}
}
