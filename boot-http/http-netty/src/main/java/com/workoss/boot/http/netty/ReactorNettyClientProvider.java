package com.workoss.boot.http.netty;

import com.workoss.boot.http.HttpClient;
import com.workoss.boot.http.HttpClientProvider;

public class ReactorNettyClientProvider implements HttpClientProvider {

	@Override
	public HttpClient create() {
		return new NettyAsyncHttpClientBuilder().build();
	}

}
