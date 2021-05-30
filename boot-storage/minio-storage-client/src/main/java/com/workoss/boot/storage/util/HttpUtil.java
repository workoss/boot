package com.workoss.boot.storage.util;

import com.workoss.boot.annotation.lang.NonNull;
import com.workoss.boot.annotation.lang.Nullable;
import com.workoss.boot.util.StringUtils;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * http 工具类
 *
 * @author workoss
 */
public class HttpUtil implements Closeable {

	private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

	private static OkHttpClient httpClient = null;
	/**
	 * 默认content 类型
	 */
	private static final String DEFAULT_CONTENT_TYPE = "application/json";
	/**
	 * 默认请求超时时间30s
	 */
	private static final int DEFAUL_TTIME_OUT = 15000;

	private static final int DEFAULT_HTTP_KEEP_TIME = 15000;

	private final static Object syncLock = new Object(); // 相当于线程锁,用于线程安全


	private HttpUtil() {

	}

	private static OkHttpClient getHttpClient() {
		if (httpClient != null) {
			return httpClient;
		}
		synchronized (syncLock) {
			if (httpClient != null) {
				return httpClient;
			}
			httpClient = new OkHttpClient.Builder()
					.connectTimeout(DEFAUL_TTIME_OUT, TimeUnit.SECONDS)
					.callTimeout(DEFAUL_TTIME_OUT, TimeUnit.SECONDS)
					.readTimeout(DEFAUL_TTIME_OUT, TimeUnit.SECONDS)
					.writeTimeout(DEFAUL_TTIME_OUT, TimeUnit.SECONDS)
					.hostnameVerifier((hostname, session) -> true)
					.build();
		}
		return httpClient;
	}

	public static String executePost(@NonNull String url, @Nullable String jsonParam,
									 @Nullable Map<String, String> headers) {
		long startTime = System.currentTimeMillis();
		if (StringUtils.isBlank(jsonParam)) {
			jsonParam = StringUtils.EMPTY;
		}
		RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonParam);
		Request.Builder builder = new Request.Builder()
				.url(url)
				.post(requestBody);
		if (headers!=null){
			headers.entrySet().forEach(stringStringEntry -> {
				builder.header(stringStringEntry.getKey(),stringStringEntry.getValue());
			});
		}
		try (Response response = getHttpClient().newCall(builder.build()).execute()) {
			String respBody = null;
			if (response.body() != null) {
				respBody = response.body().string();
			}
			if (response.isSuccessful()) {
				return respBody;
			}
			throw new RuntimeException("status:" + response.code() + "\nbody:" + respBody);
		} catch (Exception e) {
			log.error("execute post request exception, url:{}, cost time(ms):{}, exception:", url,
					(System.currentTimeMillis() - startTime), e);
			throw new RuntimeException(e);
		}
	}


	public static boolean checkUrlIsValid(String url, int timeoutMills) {
		OkHttpClient client = new OkHttpClient.Builder()
				.connectTimeout(timeoutMills, TimeUnit.SECONDS)
				.callTimeout(timeoutMills, TimeUnit.SECONDS)
				.readTimeout(timeoutMills, TimeUnit.SECONDS)
				.writeTimeout(timeoutMills, TimeUnit.SECONDS)
				.build();
		Request request = new Request.Builder().url(url).build();
		try (Response response = client.newCall(request).execute()) {
			log.info("【OKHTTP】checkUrlIsValid:{} statusCode:{}", url, response.code());
			return true;
		} catch (IOException e) {
			log.warn("【OKHTTP】checkUrlIsValid:{} ERROR:{}", url, e.getMessage());
		}
		return false;
	}


	public static void main(String[] args) {
		boolean check = checkUrlIsValid("https://oss-cn-shenzhen.aliyuncs.com", 200);
		System.out.println(check);
	}


	@Override
	public void close() throws IOException {

	}
}
