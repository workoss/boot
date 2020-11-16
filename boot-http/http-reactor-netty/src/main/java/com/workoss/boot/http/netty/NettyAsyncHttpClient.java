package com.workoss.boot.http.netty;

import com.workoss.boot.http.HttpClient;
import com.workoss.boot.http.HttpRequest;
import com.workoss.boot.http.HttpResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpMethod;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.NettyOutbound;
import reactor.netty.http.client.HttpClientRequest;
import reactor.netty.http.client.HttpClientResponse;
import java.util.Objects;
import java.util.function.BiFunction;

class NettyAsyncHttpClient implements HttpClient {

	private final reactor.netty.http.client.HttpClient nettyClient;

	private final boolean disableBufferCopy;

	NettyAsyncHttpClient() {
		this(reactor.netty.http.client.HttpClient.create(), false);
	}

	public NettyAsyncHttpClient(reactor.netty.http.client.HttpClient nettyClient, boolean disableBufferCopy) {
		this.disableBufferCopy = disableBufferCopy;
		this.nettyClient = nettyClient;
	}

	@Override
	public Mono<HttpResponse> send(final HttpRequest request) {
		Objects.requireNonNull(request.getHttpMethod(), "'request.getHttpMethod()' cannot be null.");
		Objects.requireNonNull(request.getUrl(), "'request.getUrl()' cannot be null.");
		Objects.requireNonNull(request.getUrl().getProtocol(), "'request.getUrl().getProtocol()' cannot be null.");
		return nettyClient.request(HttpMethod.valueOf(request.getHttpMethod().toString()))
				.uri(request.getUrl().toString()).send(bodySendDelegate(request))
				.responseConnection(responseDelegate(request, disableBufferCopy)).single();
	}

	private static BiFunction<HttpClientRequest, NettyOutbound, Publisher<Void>> bodySendDelegate(
			final HttpRequest restRequest) {
		return (reactorNettyRequest, reactorNettyOutbound) -> {
			restRequest.getHeaders().entrySet().stream().filter(stringListEntry -> stringListEntry.getValue() != null)
					.forEach(stringListEntry -> {
						stringListEntry.getValue().stream().filter(value -> value != null).forEach(value -> {
							reactorNettyRequest.addHeader(stringListEntry.getKey(), value);
						});
					});
			if (restRequest.getBody() != null) {
				Flux<ByteBuf> nettyByteBufFlux = restRequest.getBody().map(Unpooled::wrappedBuffer);
				return reactorNettyOutbound.send(nettyByteBufFlux);
			}
			else {
				return reactorNettyOutbound;
			}
		};
	}

	private static BiFunction<HttpClientResponse, Connection, Publisher<HttpResponse>> responseDelegate(
			final HttpRequest restRequest, final boolean disableBufferCopy) {
		return (reactorNettyResponse, reactorNettyConnection) -> Mono.just(new ReactorNettyHttpResponse(
				reactorNettyResponse, reactorNettyConnection, restRequest, disableBufferCopy));
	}

}
