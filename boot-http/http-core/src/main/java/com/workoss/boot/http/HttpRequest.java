package com.workoss.boot.http;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;

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


	public HttpRequest(HttpMethod httpMethod,String url){
		this.httpMethod = httpMethod;
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("'url' must be a valid URL",e);
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

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public void setHeaders(HttpHeaders headers) {
		this.headers = headers;
	}

	public Flux<ByteBuffer> getBody() {
		return body;
	}

	public void setBody(Flux<ByteBuffer> body) {
		this.body = body;
	}
}
