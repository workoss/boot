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
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * http client util
 *
 * @author workoss
 **/
@SuppressWarnings("ALL")
public class HttpUtil implements AutoCloseable {

	private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

	private static PoolingHttpClientConnectionManager cm = null;

	private static CloseableHttpClient httpClient = null;

	/**
	 * 默认content 类型
	 */
	private static final String DEFAULT_CONTENT_TYPE = "application/json";

	/**
	 * 默认请求超时时间30s
	 */
	private static final int DEFAUL_TTIME_OUT = 15000;

	private static final int COUNT = 32;

	private static final int TOTAL_COUNT = 1000;

	private static final int DEFAULT_HTTP_KEEP_TIME = 15000;

	private final static Object syncLock = new Object(); // 相当于线程锁,用于线程安全

	private HttpUtil() {

	}

	private static CloseableHttpClient getHttpClient() {
		if (httpClient != null) {
			return httpClient;
		}
		synchronized (syncLock) {
			if (httpClient != null) {
				return httpClient;
			}
			cm = new PoolingHttpClientConnectionManager();
			cm.setDefaultMaxPerRoute(COUNT);
			cm.setMaxTotal(TOTAL_COUNT);
			httpClient = HttpClients.custom().setKeepAliveStrategy(defaultStrategy).setConnectionManager(cm).build();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					cm.shutdown();
				}
			});
		}
		return httpClient;
	}

	/**
	 * Http connection keepAlive 设置
	 */
	private static ConnectionKeepAliveStrategy defaultStrategy = new ConnectionKeepAliveStrategy() {
		@Override
		public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
			HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
			int keepTime = DEFAULT_HTTP_KEEP_TIME;
			while (it.hasNext()) {
				HeaderElement he = it.nextElement();
				String param = he.getName();
				String value = he.getValue();
				if (value != null && "timeout".equalsIgnoreCase(param)) {
					try {
						return Long.parseLong(value) * 1000;
					} catch (Exception e) {
						log.error("format KeepAlive timeout exception, exception:", e);
					}
				}
			}
			return keepTime * 1000;
		}
	};

	/**
	 * 执行http post请求 默认采用Content-Type：application/json，Accept：application/json
	 *
	 * @param uri  请求地址
	 * @param data 请求数据
	 * @return string
	 */
	public static String executePost(@NonNull String uri, @Nullable String data) {
		long startTime = System.currentTimeMillis();
		HttpEntity httpEntity = null;
		HttpEntityEnclosingRequestBase method = null;
		String responseBody = "";
		try {
			method = (HttpEntityEnclosingRequestBase) getRequest(uri, HttpPost.METHOD_NAME, DEFAULT_CONTENT_TYPE, 0);
			if (data != null) {
				method.setEntity(new StringEntity(data));
			}
			HttpContext context = HttpClientContext.create();
			CloseableHttpResponse httpResponse = getHttpClient().execute(method, context);
			httpEntity = httpResponse.getEntity();
			if (httpEntity != null) {
				responseBody = EntityUtils.toString(httpEntity, "UTF-8");
			}
		} catch (Exception e) {
			if (method != null) {
				method.abort();
			}
			log.error("execute post request exception, url:{}, cost time(ms):{}, exception:", uri,
					(System.currentTimeMillis() - startTime), e);
		} finally {
			if (httpEntity != null) {
				try {
					EntityUtils.consumeQuietly(httpEntity);
				} catch (Exception e) {
					log.error("close response exception, url:{} cost time(ms):{} exception:{}, ", uri,
							(System.currentTimeMillis() - startTime), e);
				}
			}
		}
		return responseBody;
	}

	public static boolean checkUrlIsValid(String url, int timeoutMills) {
		boolean isValid = false;
		if (timeoutMills <= 0) {
			timeoutMills = DEFAUL_TTIME_OUT / 1000;
		}
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeoutMills)
				.setConnectTimeout(timeoutMills).setConnectionRequestTimeout(timeoutMills)
				.setExpectContinueEnabled(false).build();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(requestConfig);
		try (CloseableHttpResponse httpResponse = getHttpClient().execute(httpGet)) {
			log.info("【HTTPCLIENT】checkUrlIsValid:{} statusCode:{}", url, httpResponse.getStatusLine().getStatusCode());
			return true;
		} catch (Exception e) {
			log.warn("【HTTPCLIENT】checkUrlIsValid:{} ERROR:{}", url, e.getMessage());
		}
		return isValid;
	}

	public static String doPostJson(@NonNull String url, @Nullable String jsonParam,
									@Nullable Map<String, String> headers) {
		HttpPost httpPost = null;
		CloseableHttpResponse response = null;
		try {
			httpPost = new HttpPost(url);
			// addHeader，如果Header没有定义则添加，已定义则不变，setHeader会重新赋值
			httpPost.addHeader("Content-type", "application/json;charset=utf-8");
			httpPost.setHeader("Accept", "application/json");
			if (jsonParam != null) {
				StringEntity entity = new StringEntity(jsonParam, StandardCharsets.UTF_8);
				httpPost.setEntity(entity);
			}
			// 是否有header
			if (headers != null && headers.size() > 0) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					httpPost.addHeader(entry.getKey(), entry.getValue());
				}
			}
			// 执行请求
			response = getHttpClient().execute(httpPost);
			// 判断返回状态是否为200
			String resp = EntityUtils.toString(response.getEntity(), "UTF-8");
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				log.warn("[HTTP] url:{},status:{},resp:{}", url, response.getStatusLine(), resp);
			}
			return resp;
		} catch (Exception e) {
			throw new RuntimeException("[send POST request error：]", e);
		} finally {
			try {
				httpPost.releaseConnection();
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {

			}
		}
	}

	/**
	 * 创建请求
	 *
	 * @param uri         请求url
	 * @param methodName  请求的方法类型
	 * @param contentType contentType类型
	 * @param timeout     超时时间
	 * @return HttpRequestBase 返回类型
	 */
	public static HttpRequestBase getRequest(@NonNull String uri, @NonNull String methodName,
											 @Nullable String contentType, int timeout) {
		HttpRequestBase method = null;
		if (timeout <= 0) {
			timeout = DEFAUL_TTIME_OUT;
		}
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout * 1000)
				.setConnectTimeout(timeout * 1000).setConnectionRequestTimeout(timeout * 1000)
				.setExpectContinueEnabled(false).build();
		if (HttpPut.METHOD_NAME.equalsIgnoreCase(methodName)) {
			method = new HttpPut(uri);
		} else if (HttpPost.METHOD_NAME.equalsIgnoreCase(methodName)) {
			method = new HttpPost(uri);
		} else if (HttpGet.METHOD_NAME.equalsIgnoreCase(methodName)) {
			method = new HttpGet(uri);
		} else {
			method = new HttpPost(uri);
		}
		if (StringUtils.isEmpty(contentType)) {
			contentType = DEFAULT_CONTENT_TYPE;
		}
		method.addHeader("Content-Type", contentType);
		method.addHeader("Accept", contentType);
		method.setConfig(requestConfig);
		return method;
	}

	public static String doGet(@NonNull String path, @Nullable Map<String, String> param,
							   @Nullable Map<String, String> headers) {
		HttpGet httpGet = null;
		CloseableHttpResponse response = null;
		// CloseableHttpClient httpClient = wrapClient(path);
		// 创建uri
		URIBuilder builder = null;
		try {
			builder = new URIBuilder(path);
			if (param != null) {
				for (Map.Entry<String, String> keyset : param.entrySet()) {
					builder.addParameter(keyset.getKey(), keyset.getValue());
				}
			}
			URI uri = builder.build();
			// 创建http GET请求
			httpGet = new HttpGet(uri);
			if (headers != null && headers.size() > 0) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					httpGet.addHeader(entry.getKey(), entry.getValue());
				}
			}
			if (response == null) {
				return null;
			}
			String resp = EntityUtils.toString(response.getEntity(), "UTF-8");
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				log.warn("[HTTP] url:{},status:{},resp:{}", path, response.getStatusLine(), resp);
			}
			return resp;
		} catch (Exception e) {
			throw new RuntimeException("[send Get request error：]", e);
		} finally {
			try {
				httpGet.releaseConnection();
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 执行GET 请求
	 *
	 * @param uri url
	 * @return string
	 */
	public static String executeGet(@NonNull String uri) {
		long startTime = System.currentTimeMillis();
		HttpEntity httpEntity = null;
		HttpRequestBase method = null;
		String responseBody = "";
		try {
			method = getRequest(uri, HttpGet.METHOD_NAME, DEFAULT_CONTENT_TYPE, 0);
			HttpContext context = HttpClientContext.create();
			CloseableHttpResponse httpResponse = getHttpClient().execute(method, context);
			httpEntity = httpResponse.getEntity();
			if (httpEntity != null) {
				responseBody = EntityUtils.toString(httpEntity, "UTF-8");
				log.info("request URL: {} Return status code：{}", uri, httpResponse.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			if (method != null) {
				method.abort();
			}
			log.error("execute get request exception, url:{},cost time(ms):{},exception:", uri,
					(System.currentTimeMillis() - startTime), e);
		} finally {
			if (httpEntity != null) {
				try {
					EntityUtils.consumeQuietly(httpEntity);
				} catch (Exception e) {
					log.error("close response exception, url:{}, ,cost time(ms):{}, exception:", uri,
							(System.currentTimeMillis() - startTime), e);
				}
			}
		}
		return responseBody;
	}

	@Override
	public void close() throws IOException {
		if (httpClient != null) {
			httpClient.close();
		}
	}

}
