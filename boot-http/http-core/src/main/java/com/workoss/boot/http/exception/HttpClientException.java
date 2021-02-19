package com.workoss.boot.http.exception;

public class HttpClientException extends RuntimeException {

	public HttpClientException(String s) {
		super(s);
	}

	public HttpClientException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public HttpClientException(Throwable throwable) {
		super(throwable);
	}

	protected HttpClientException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}

}
