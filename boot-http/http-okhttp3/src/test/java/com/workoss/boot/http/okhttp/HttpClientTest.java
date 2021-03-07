package com.workoss.boot.http.okhttp;

import com.workoss.boot.http.HttpClient;
import com.workoss.boot.http.HttpClientResponse;
import com.workoss.boot.http.HttpMethod;
import com.workoss.boot.http.MediaType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;

public class HttpClientTest {

	@Test
	void test01() {
		HttpClient httpClient = HttpClient.builder()
				.filter((request, nextHandler) -> {
					System.out.println("-----1-------");
					HttpClientResponse response = nextHandler.handler(request);
					System.out.println("-----11-------");
					return response;
				})
				.filter((request, nextHandler) -> {
					System.out.println("-----2-------");
					HttpClientResponse response = nextHandler.handler(request);
					System.out.println("-----22-------");
					return response;
				})
				.filters(httpClientFilters -> {
					httpClientFilters.add((request, nextHandler) -> {
						System.out.println("-----3-------");
						HttpClientResponse response = nextHandler.handler(request);
						System.out.println("-----33-------");
						return response;
					});
				})
				.build();

		try (HttpClientResponse response = httpClient.method(HttpMethod.GET)
				.uri(URI.create("https://www.baidu.com"))
				.contentType(MediaType.TEXT_PLAIN).execute()) {
			System.out.println(response.stringBody());
			System.out.println(response.status());
			System.out.println(response.headers());
			System.out.println(response.cookies());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
