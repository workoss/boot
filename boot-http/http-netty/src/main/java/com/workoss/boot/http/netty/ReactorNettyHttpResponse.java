package com.workoss.boot.http.netty;

import com.workoss.boot.http.HttpHeaders;
import com.workoss.boot.http.HttpRequest;
import com.workoss.boot.http.HttpResponse;
import com.workoss.boot.http.util.HttpToolUtil;
import io.netty.buffer.ByteBuf;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.Connection;
import reactor.netty.http.client.HttpClientResponse;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class ReactorNettyHttpResponse extends HttpResponse {

	private final HttpClientResponse reactorNettyResponse;

	private final Connection reactorNettyConnection;

	private final boolean disableBufferCopy;

	protected ReactorNettyHttpResponse(HttpClientResponse reactorNettyResponse, Connection reactorNettyConnection,
			HttpRequest httpRequest, boolean disableBufferCopy) {
		super(httpRequest);
		this.reactorNettyResponse = reactorNettyResponse;
		this.reactorNettyConnection = reactorNettyConnection;
		this.disableBufferCopy = disableBufferCopy;
	}

	@Override
	public int getStatusCode() {
		return reactorNettyResponse.status().code();
	}

	@Override
	public String getHeaderValue(String name) {
		return reactorNettyResponse.responseHeaders().get(name);
	}

	@Override
	public HttpHeaders getHeaders() {
		HttpHeaders httpHeaders = new HttpHeaders();
		reactorNettyResponse.responseHeaders().forEach(entry -> httpHeaders.set(entry.getKey(), entry.getValue()));
		return httpHeaders;
	}

	@Override
	public Flux<ByteBuffer> getBody() {
		return toByteBufFlux().doFinally(signalType -> close())
				.map(byteBuf -> this.disableBufferCopy ? byteBuf.nioBuffer() : deepCopyBuffer(byteBuf));
	}

	@Override
	public Mono<byte[]> getBodyAsByteArray() {
		return toByteBufFlux().aggregate().asByteArray().doFinally(signalType -> close());
	}

	@Override
	public Mono<String> getBodyAsString() {
		return getBodyAsByteArray().map(bytes -> HttpToolUtil.bomAwareToString(bytes,
				reactorNettyResponse.responseHeaders().get(HttpHeaders.CONTENT_TYPE)));
	}

	@Override
	public Mono<String> getBodyAsString(Charset charset) {
		return toByteBufFlux().aggregate().asString(charset).doFinally(signalType -> close());
	}

	@Override
	public void close() {
		if (!reactorNettyConnection.isDisposed()) {
			reactorNettyConnection.channel().eventLoop().execute(reactorNettyConnection::dispose);
		}
	}

	private ByteBufFlux toByteBufFlux() {
		return reactorNettyConnection.inbound().receive();
	}

	private static ByteBuffer deepCopyBuffer(ByteBuf byteBuf) {
		ByteBuffer buffer = ByteBuffer.allocate(byteBuf.readableBytes());
		byteBuf.readBytes(buffer);
		buffer.rewind();
		return buffer;
	}

}
