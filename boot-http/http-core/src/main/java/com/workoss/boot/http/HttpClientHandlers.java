package com.workoss.boot.http;

public class HttpClientHandlers {

	public static HttpClientHandler create(HttpClientConnector connector) {
		return new DefaultHttpClientHandler(connector);
	}

	private static class DefaultHttpClientHandler implements HttpClientHandler {

		private HttpClientConnector connector;

		public DefaultHttpClientHandler(HttpClientConnector connector) {
			this.connector = connector;
		}

		@Override
		public HttpClientResponse handler(HttpClientRequest request) {
			return connector.connect(request, request1 -> {
				System.out.println("-----------");
				return null;
			});
		}

	}

}
