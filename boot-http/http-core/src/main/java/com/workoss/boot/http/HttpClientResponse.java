package com.workoss.boot.http;

import com.workoss.boot.util.MultiValueMap;

import java.io.Closeable;
import java.io.InputStream;
import java.nio.ByteBuffer;

public interface HttpClientResponse extends Closeable {

	HttpStatus status();

	int rawStatusCode();

	HttpHeaders headers();

	MultiValueMap<String, String> cookies();

	ByteBuffer byteBody();

	InputStream body();

	String stringBody();

}
