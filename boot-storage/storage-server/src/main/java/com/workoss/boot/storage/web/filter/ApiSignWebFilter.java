package com.workoss.boot.storage.web.filter;

import com.yifengx.popeye.util.http.HttpConstants;
import com.yifengx.popeye.util.http.HttpMethod;
import com.yifengx.popeye.util.http.SdkHttpRequest;
import com.yifengx.popeye.util.http.SignableHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

@Slf4j
public class ApiSignWebFilter implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain webFilterChain) {
		Mono<ServerHttpRequest> requestMono = ReactiveRequestContextHolder.getRequest();
		if (requestMono != null) {
			return requestMono.flatMap(serverHttpRequest -> {
				return webFilterChain.filter(exchange);
			});
		}

		// 获取header 参数
		ServerHttpRequest request = exchange.getRequest();
		System.out.println("uri:" + request.getURI());
		System.out.println("path:" + request.getPath());
		System.out.println("queryParams:" + request.getQueryParams());
		request.getHeaders().entrySet().stream().forEach(stringListEntry -> {
			System.out.println(stringListEntry.getKey() + ":" + stringListEntry.getValue());
		});

		Map<String, String> singleHeadMap = request.getHeaders().toSingleValueMap();
		String sdkDateTime = singleHeadMap.get(HttpConstants.Sign.X_SDK_DATE);
		String contentType = singleHeadMap.get(HttpConstants.Sign.CONTENT_TYPE);
		if (contentType == null) {
			return webFilterChain.filter(exchange);
		}
		if (contentType.toLowerCase().contains("application/x-www-form-urlencoded")) {
			return exchange.getFormData().flatMap(stringStringMultiValueMap -> {
				stringStringMultiValueMap.keySet().stream().forEach(key -> {
					System.out.println("-------------------FormData---------------");
					System.out.println(key + ":" + stringStringMultiValueMap.get(key));
					System.out.println("-------------------FormData---------------");
				});
				return webFilterChain.filter(exchange);
			});
		}
		else if (contentType.toLowerCase().contains("multipart/form-data")) {
			return exchange.getMultipartData().flatMap(stringPartMultiValueMap -> {
				stringPartMultiValueMap.keySet().stream().forEach(key -> {
					System.out.println("-------------------MultipartData---------------");
					System.out.println(key + ":" + stringPartMultiValueMap.getFirst(key).toString());
					System.out.println("-------------------MultipartData---------------");
				});
				return webFilterChain.filter(exchange);
			});
		}

		SignableHttpRequest sdkHttpRequest = SdkHttpRequest.newBuilder().uri(request.getURI())
				.method(HttpMethod.valueOf(request.getMethodValue().toUpperCase(Locale.ROOT)))
				.contentType("application/json")
				// .body("{\n\"id\":\"1\"}")
				.build();

		String contentLength = request.getHeaders().toSingleValueMap().get(HttpHeaders.CONTENT_LENGTH);
		boolean hasBody = contentLength != null && Integer.parseInt(contentLength) > 0;
		if (!hasBody) {
			return webFilterChain.filter(exchange);
		}
		return DataBufferUtils.join(request.getBody()).flatMap(dataBuffer -> {
			// 创建一个容量为dataBuffer容量大小的字节数组
			byte[] bytes = new byte[dataBuffer.readableByteCount()];
			// dataBuffer类容读取到bytes中
			dataBuffer.read(bytes);
			// 释放缓冲区
			DataBufferUtils.release(dataBuffer);
			System.out.println("-------------------body---------------");
			System.out.println(new String(bytes, StandardCharsets.UTF_8));
			System.out.println("-------------------body---------------");

			Flux<DataBuffer> cachedFlux = Flux.defer(() -> {
				DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
				DataBufferUtils.retain(buffer);
				return Mono.just(buffer);
			});

			// 由于原来的request请求参数被消费，需要提供新的请求
			ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
				@Override
				public Flux<DataBuffer> getBody() {
					return cachedFlux;
				}
			};
			// 创建新的exchange并构建解析的数据
			ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
			return webFilterChain.filter(mutatedExchange);
		});
	}

}
