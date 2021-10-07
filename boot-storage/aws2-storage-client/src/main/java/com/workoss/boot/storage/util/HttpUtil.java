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
package com.workoss.boot.storage.util;

import com.workoss.boot.annotation.lang.NonNull;
import com.workoss.boot.annotation.lang.Nullable;
import com.workoss.boot.storage.exception.StorageException;
import com.workoss.boot.util.exception.ExceptionUtils;
import com.workoss.boot.util.collection.CollectionUtils;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.internal.http.async.SimpleHttpContentPublisher;
import software.amazon.awssdk.http.*;
import software.amazon.awssdk.http.async.AsyncExecuteRequest;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.async.SdkAsyncHttpResponseHandler;
import software.amazon.awssdk.http.async.SdkHttpContentPublisher;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.Lazy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

import static software.amazon.awssdk.http.Header.CONTENT_LENGTH;

/**
 * NettyNioAsyncHttpClient
 *
 * @author workoss
 **/
@SuppressWarnings("ALL")
public final class HttpUtil implements AutoCloseable {

	private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

	/**
	 * 默认content 类型
	 */
	private static final String DEFAULT_CONTENT_TYPE = "application/json";

	/**
	 * 默认请求超时时间30s
	 */
	private static final Duration DEFAULT_TIME_OUT = Duration.ofSeconds(30);

	private static final ConcurrentMap<Duration, SdkAsyncHttpClient> CLIENT_CACHE = new ConcurrentHashMap<>();

	private static Lazy<NettyNioAsyncHttpClient.Builder> httpClientLazy = new Lazy<>(
			() -> createHttpClientBuilder(DEFAULT_TIME_OUT));

	private HttpUtil() {

	}

	static NettyNioAsyncHttpClient.Builder createHttpClientBuilder(Duration timeout) {
		return NettyNioAsyncHttpClient.builder().protocol(Protocol.HTTP1_1).connectionTimeout(timeout)
				.readTimeout(timeout).writeTimeout(timeout);
	}

	static SdkAsyncHttpClient getHttpClient(Duration timeout) {
		if (timeout == null) {
			timeout = DEFAULT_TIME_OUT;
		}
		if (CLIENT_CACHE.size() > 8) {
			CLIENT_CACHE.clear();
		}
		return CLIENT_CACHE.getOrDefault(timeout, createHttpClientBuilder(timeout).build());
	}

	/**
	 * 执行http post请求 默认采用Content-Type：application/json，Accept：application/json
	 * @param uri 请求地址
	 * @param data 请求数据
	 * @return string
	 */
	public static String executePost(@NonNull String uri, @Nullable String data) {
		return doPostJson(uri, data, null);
	}

	public static boolean checkUrlIsValid(String url, int timeoutMills) {
		boolean isValid = false;
		try {
			SdkHttpFullResponse response = execute(url, SdkHttpMethod.GET, null, null, null,
					Duration.ofMillis(timeoutMills));
			return response != null && 404 != response.statusCode();
		}
		catch (Exception e) {
			log.warn("【HTTPCLIENT】checkUrlIsValid:{} ERROR:{}", url, ExceptionUtils.toString(e));
			return false;
		}
	}

	public static String doPostJson(@NonNull String url, @Nullable String jsonParam,
			@Nullable Map<String, String> headers) {
		if (headers == null) {
			headers = new HashMap<>(8);
		}
		headers.put(Header.CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
		SdkHttpFullResponse response = execute(url, SdkHttpMethod.POST, null, headers, jsonParam, DEFAULT_TIME_OUT);
		if (response == null) {
			throw new StorageException("request post has no response");
		}
		AbortableInputStream abortableInputStream = response.content().orElse(null);
		try {
			return abortableInputStream != null ? IoUtils.toUtf8String(abortableInputStream) : null;
		}
		catch (IOException e) {
			throw new StorageException(ExceptionUtils.toString(e));
		}
	}

	public static String doGet(@NonNull String url, @Nullable Map<String, String> param,
			@Nullable Map<String, String> headers) {
		SdkHttpFullResponse response = execute(url, SdkHttpMethod.GET, param, headers, null, DEFAULT_TIME_OUT);
		if (response == null) {
			throw new StorageException("request get has no response");
		}
		AbortableInputStream abortableInputStream = response.content().orElse(null);
		try {
			return abortableInputStream != null ? IoUtils.toUtf8String(abortableInputStream) : null;
		}
		catch (IOException e) {
			throw new StorageException(ExceptionUtils.toString(e));
		}
	}

	/**
	 * 执行GET 请求
	 * @param uri url
	 * @return string
	 */
	public static String executeGet(@NonNull String uri) {
		return doGet(uri, null, null);
	}

	static SdkHttpFullResponse execute(String url, SdkHttpMethod method, Map<String, String> params,
			Map<String, String> headers, String body, Duration timeout) {
		SdkHttpFullRequest.Builder requestBuilder = SdkHttpFullRequest.builder().uri(URI.create(url)).method(method);
		if (CollectionUtils.isNotEmpty(params)) {
			for (Map.Entry<String, String> stringStringEntry : params.entrySet()) {
				requestBuilder.putRawQueryParameter(stringStringEntry.getKey(), stringStringEntry.getValue());
			}
		}
		if (CollectionUtils.isNotEmpty(headers)) {
			for (Map.Entry<String, String> stringStringEntry : headers.entrySet()) {
				requestBuilder.putHeader(stringStringEntry.getKey(), stringStringEntry.getValue());
			}
		}

		SdkHttpContentPublisher contentPublisher = null;

		CompletableFuture<ByteArrayOutputStream> streamFuture = new CompletableFuture<>();

		SdkHttpFullResponse.Builder responseBuilder = SdkHttpFullResponse.builder();

		SdkAsyncHttpResponseHandler sdkAsyncHttpResponseHandler = new SdkAsyncHttpResponseHandler() {
			@Override
			public void onHeaders(SdkHttpResponse response) {
				responseBuilder.headers(response.headers()).statusCode(response.statusCode())
						.statusText(response.statusText().orElse(null));
			}

			@Override
			public void onStream(Publisher<ByteBuffer> stream) {
				stream.subscribe(new BytesSubscriber(streamFuture));
			}

			@Override
			public void onError(Throwable error) {
				streamFuture.completeExceptionally(error);
			}
		};
		SdkHttpFullRequest request = requestBuilder.build();

		if (body != null) {
			contentPublisher = new SdkHttpContentPublisherAdapter(
					AsyncRequestBody.fromString(body, StandardCharsets.UTF_8));
		}
		else {
			contentPublisher = new SimpleHttpContentPublisher(request);
		}

		request = getRequestWithContentLength(request, contentPublisher);

		AsyncExecuteRequest executeRequest = AsyncExecuteRequest.builder().request(request)
				.requestContentPublisher(contentPublisher).responseHandler(sdkAsyncHttpResponseHandler).build();

		try {
			CompletableFuture<Void> execute = getHttpClient(timeout).execute(executeRequest);
			ByteArrayOutputStream byteArrayOutputStream = streamFuture.get(1, TimeUnit.MINUTES);
			return responseBuilder
					.content(AbortableInputStream.create(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())))
					.build();
		}
		catch (InterruptedException e) {
			throw new StorageException("500", ExceptionUtils.toShortString(e, 2));
		}
		catch (ExecutionException e) {
			throw new StorageException("500", ExceptionUtils.toShortString(e, 2));
		}
		catch (TimeoutException e) {
			throw new StorageException("500", ExceptionUtils.toShortString(e, 2));
		}
	}

	@Override
	public void close() throws IOException {
		CLIENT_CACHE.forEach((duration, sdkAsyncHttpClient) -> {
			sdkAsyncHttpClient.close();
		});
	}

	private static SdkHttpFullRequest getRequestWithContentLength(SdkHttpFullRequest request,
			SdkHttpContentPublisher requestProvider) {
		if (shouldSetContentLength(request, requestProvider)) {
			return request.toBuilder().putHeader(CONTENT_LENGTH, String.valueOf(requestProvider.contentLength().get()))
					.build();
		}
		return request;
	}

	private static boolean shouldSetContentLength(SdkHttpFullRequest request, SdkHttpContentPublisher requestProvider) {

		if (request.method() == SdkHttpMethod.GET || request.method() == SdkHttpMethod.HEAD
				|| request.firstMatchingHeader(CONTENT_LENGTH).isPresent()) {
			return false;
		}

		return Optional.ofNullable(requestProvider).flatMap(SdkHttpContentPublisher::contentLength).isPresent();
	}

	private static final class SdkHttpContentPublisherAdapter implements SdkHttpContentPublisher {

		private final AsyncRequestBody asyncRequestBody;

		private SdkHttpContentPublisherAdapter(AsyncRequestBody asyncRequestBody) {
			this.asyncRequestBody = asyncRequestBody;
		}

		@Override
		public Optional<Long> contentLength() {
			return asyncRequestBody.contentLength();
		}

		@Override
		public void subscribe(Subscriber<? super ByteBuffer> s) {
			asyncRequestBody.subscribe(s);
		}

	}

	static class BytesSubscriber implements Subscriber<ByteBuffer> {

		private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		private final CompletableFuture<ByteArrayOutputStream> streamFuture;

		private Subscription subscription;

		private boolean dataWritten = false;

		private BytesSubscriber(CompletableFuture<ByteArrayOutputStream> streamFuture) {
			this.streamFuture = streamFuture;
		}

		@Override
		public void onSubscribe(Subscription subscription) {
			this.subscription = subscription;
			subscription.request(Long.MAX_VALUE);
		}

		@Override
		public void onNext(ByteBuffer byteBuffer) {
			dataWritten = true;
			try {
				baos.write(BinaryUtils.copyBytesFrom(byteBuffer));
				this.subscription.request(1);
			}
			catch (IOException e) {
				streamFuture.completeExceptionally(e);
			}
		}

		@Override
		public void onError(Throwable throwable) {
			streamFuture.completeExceptionally(throwable);
		}

		@Override
		public void onComplete() {
			streamFuture.complete(dataWritten ? baos : null);
		}

	}

}
