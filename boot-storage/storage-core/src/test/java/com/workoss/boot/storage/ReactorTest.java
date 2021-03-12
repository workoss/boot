/*
 * Copyright 2019-2021 workoss (https://www.workoss.com)
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
// package com.workoss.boot.storage;
//
// import com.workoss.boot.util.concurrent.ThreadPoolUtils;
// import io.netty.buffer.ByteBuf;
// import io.netty.buffer.ByteBufUtil;
// import io.netty.buffer.Unpooled;
// import io.netty.handler.codec.http.HttpClientCodec;
// import org.junit.jupiter.api.Test;
// import org.springframework.core.io.buffer.DataBuffer;
// import org.springframework.core.io.buffer.DataBufferUtils;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.client.reactive.ClientHttpResponse;
// import org.springframework.http.client.reactive.ReactorClientHttpConnector;
// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;
// import reactor.netty.http.client.HttpClient;
//
// import java.net.URI;
// import java.nio.ByteBuffer;
// import java.nio.charset.StandardCharsets;
//
// public class ReactorTest {
//
// @Test
// void testHttp() throws InterruptedException {
// // HttpClient.create().get().uri("https://www.baidu.com").responseContent();
// HttpClient.create().get().uri("https://www.baidu.com").responseConnection((httpClientResponse,
// connection) -> {
// return connection.inbound().receive().doOnSubscribe(subscription -> {
//
// }).map(byteBuf -> {
//
// return byteBuf.readByte();
// });
// });
//
// ReactorClientHttpConnector connector = new ReactorClientHttpConnector();
// ClientHttpResponse response = connector
// .connect(HttpMethod.GET, URI.create("https://www.baidu.com"), clientHttpRequest -> {
//
// return Mono.empty();
// }).block();
//
// System.out.println(response.getStatusCode());
// System.out.println(response.getCookies());
// System.out.println(response.getHeaders());
//
// String resp = DataBufferUtils.join(response.getBody()).flatMap(dataBuffer -> {
// byte[] bytes = new byte[dataBuffer.readableByteCount()];
// // dataBuffer类容读取到bytes中
// dataBuffer.read(bytes);
// // 释放缓冲区
// DataBufferUtils.release(dataBuffer);
// return Mono.just(new String(bytes, StandardCharsets.UTF_8));
// }).block();
//
// System.out.println(resp);
//
// }
//
// }
