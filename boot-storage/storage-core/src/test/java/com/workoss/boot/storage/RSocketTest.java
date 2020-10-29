package com.workoss.boot.storage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.netty.channel.ChannelOperations;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.function.Function;

@Slf4j
public class RSocketTest {

	@BeforeAll
	static void setUp() {

	}

	@Test
	void testHttp() {
		ConnectionProvider connectionProvider = ConnectionProvider.builder("http").maxConnections(500).build();
		for (int i = 0; i < 1; i++) {
			String resp = HttpClient.create(connectionProvider).baseUrl("https://www.baidu.com").get().responseContent()
					.aggregate().asString().block();
			log.info("resp:{}", resp);
		}
		String resp = HttpClient.create(connectionProvider).headers(entries -> {
			entries.add("customClient", "myself");
		}).doOnRequest((httpClientRequest, connection) -> {
			System.out.println(httpClientRequest.requestHeaders());
		}).doOnRequestError((httpClientRequest, throwable) -> {
			log.error("doOnRequestError", throwable);
		}).get().uri("https://www.baidu.com").responseConnection((httpClientResponse, connection) -> {
			System.out.println(httpClientResponse.status());
			System.out.println(httpClientResponse.responseHeaders());
			System.out.println(httpClientResponse.cookies());
			return connection.inbound().receive().doOnSubscribe(subscription -> {

			}).map(byteBuf -> {
				byte[] bytes = new byte[byteBuf.readableBytes()];
				byteBuf.readBytes(bytes);
				return new String(bytes, StandardCharsets.UTF_8);
			});
		}).doOnError(throwable -> {
			log.error("doOnError", throwable);
		}).blockLast();

//		System.out.println(resp);

		 resp = HttpClient.create().get().uri("https://www.qq.com")
				.responseSingle((httpClientResponse, byteBufMono) -> {
					System.out.println(httpClientResponse.status());
					return byteBufMono.asString(StandardCharsets.UTF_8);
				}).block();
//		System.out.println(resp);

	}

	static final Function<ChannelOperations<?, ?>, Publisher<ByteBuf>> contentReceiver = ChannelOperations::receive;

}
