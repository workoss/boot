package com.workoss.boot.http.netty;

import com.workoss.boot.http.HttpConnector;
import com.workoss.boot.http.HttpMethod;
import com.workoss.boot.http.HttpRequest;
import com.workoss.boot.http.HttpResponse;
import com.workoss.boot.util.Assert;
import com.workoss.boot.util.collection.CollectionUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.NettyOutbound;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientRequest;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ReactorHttpConnector implements HttpConnector {

	private final static Function<HttpClient, HttpClient> defaultInitializer = client -> client.compress(true);

	private final HttpClient httpClient;

	public ReactorHttpConnector() {
		this.httpClient = defaultInitializer.apply(HttpClient.create());
	}

	public ReactorHttpConnector(Function<HttpClient, HttpClient> mapper) {
		this.httpClient = defaultInitializer.andThen(mapper).apply(HttpClient.create());
	}

	/**
	 * Constructor with a pre-configured {@code HttpClient} instance.
	 * @param httpClient the client to use
	 * @since 5.1
	 */
	public ReactorHttpConnector(HttpClient httpClient) {
		Assert.notNull(httpClient, "HttpClient is required");
		this.httpClient = httpClient;
	}

	@Override
	public Mono<HttpResponse> connect(HttpMethod method, URI uri,
			Function<? super HttpRequest, Mono<Void>> requestCallback) {
		AtomicReference<ReactorNettyHttpResponse> responseRef = new AtomicReference<>();
		AtomicReference<HttpRequest> requestRef = new AtomicReference<>();
		return this.httpClient.request(io.netty.handler.codec.http.HttpMethod.valueOf(method.name()))
				.uri(uri.toString())
//				.send((clientRequest, outbound) -> bodySendDelegate())
				.responseConnection((response, connection) -> {
					responseRef.set(new ReactorNettyHttpResponse(response, connection, requestRef.get(), false));
					return Mono.just((HttpResponse) responseRef.get());
				}).next().doOnCancel(() -> {
					HttpRequest request = requestRef.get();
					ReactorNettyHttpResponse response = responseRef.get();
					if (response != null) {
						response.close();
					}
				});
	}

	private static BiFunction<HttpClientRequest, NettyOutbound, Publisher<Void>> bodySendDelegate(
			final HttpRequest restRequest) {
		return (reactorNettyRequest, reactorNettyOutbound) -> {
			restRequest.getHeaders().entrySet().stream()
					.filter(stringListEntry -> CollectionUtils.isNotEmpty(stringListEntry.getValue()))
					.forEach(stringListEntry -> {
						reactorNettyRequest.header(stringListEntry.getKey(), stringListEntry.getValue().get(0));
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

//	private HttpRequest adaptRequest(HttpMethod method,URI uri,HttpClientRequest clientRequest,
//										   NettyOutbound nettyOutbound) {
//		return new HttpRequest(method,uri,null,nettyOutbound.);
//	}

}
