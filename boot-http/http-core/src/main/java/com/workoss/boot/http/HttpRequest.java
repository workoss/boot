package com.workoss.boot.http;

import com.workoss.boot.http.exception.HttpClientException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Slf4j
public class HttpRequest {

	private HttpMethod httpMethod;

	private URL url;

	private HttpHeaders headers;

	private Flux<ByteBuffer> body;

	public HttpRequest(HttpMethod httpMethod, URL url) {
		this.httpMethod = httpMethod;
		this.url = url;
		this.headers = new HttpHeaders();
	}

	public HttpRequest(HttpMethod httpMethod, String url) {
		this.httpMethod = httpMethod;
		try {
			this.url = new URL(url);
		}
		catch (MalformedURLException e) {
			throw new HttpClientException("'url' must be a valid URL", e);
		}
		this.headers = new HttpHeaders();
	}

	public HttpRequest(HttpMethod httpMethod, URL url, HttpHeaders headers, Flux<ByteBuffer> body) {
		this.httpMethod = httpMethod;
		this.url = url;
		this.headers = headers;
		this.body = body;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public HttpRequest setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
		return this;
	}

	public URL getUrl() {
		return url;
	}

	public HttpRequest setUrl(URL url) {
		this.url = url;
		return this;
	}

	public HttpRequest setUrl(String url) {
		try {
			this.url = new URL(url);
		}
		catch (MalformedURLException e) {
			throw new HttpClientException("'url' must be a valid URL", e);
		}
		return this;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public HttpRequest setHeaders(HttpHeaders headers) {
		this.headers = headers;
		return this;
	}

	public HttpRequest setHeader(String name, String value) {
		headers.set(name, value);
		return this;
	}

	public Flux<ByteBuffer> getBody() {
		return body;
	}

	public HttpRequest setBody(Flux<ByteBuffer> body) {
		this.body = body;
		return this;
	}

	public HttpRequest setBody(byte[] body) {
		headers.setContentLength(body.length);
		return setBody(Flux.defer(() -> Flux.just(ByteBuffer.wrap(body))));
	}

	public HttpRequest setBody(String body) {
		final byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
		return setBody(bodyBytes);
	}

	public HttpRequest copy() {
		final HttpHeaders bufferedHeaders = new HttpHeaders(headers);
		return new HttpRequest(httpMethod, url, bufferedHeaders, body);
	}

}
