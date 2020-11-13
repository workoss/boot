package com.workoss.boot.http.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.util.AttributeKey;

import java.net.SocketAddress;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class HttpProxyHandler extends ProxyHandler {

	private static final AttributeKey<String> PROXY_AUTHORIZATION_KEY = AttributeKey.newInstance("ProxyAuthorization");

	private static final String NONE = "none";
	private static final String HTTP = "http";

	private static final String CNONCE = "cnonce";
	private static final String NC = "nc";

	private static final String AUTH_BASIC = "basic";
	private static final String AUTH_DIGEST = "digest";

	private static final Pattern AUTH_SCHEME_PATTERN = Pattern.compile("^" + AUTH_DIGEST, Pattern.CASE_INSENSITIVE);

	private static final String PROXY_URI_PATH = "/";

	private static final Supplier<byte[]> NO_BODY = () -> new byte[0];

	private String authScheme = null;
	private HttpResponseStatus status;

	protected HttpProxyHandler(SocketAddress proxyAddress) {
		super(proxyAddress);
	}

	@Override
	public String protocol() {
		return HTTP;
	}

	@Override
	public String authScheme() {
		return (authScheme == null) ? NONE : authScheme;
	}

	@Override
	protected void addCodec(ChannelHandlerContext channelHandlerContext) throws Exception {

	}

	@Override
	protected void removeEncoder(ChannelHandlerContext channelHandlerContext) throws Exception {

	}

	@Override
	protected void removeDecoder(ChannelHandlerContext channelHandlerContext) throws Exception {

	}

	@Override
	protected Object newInitialMessage(ChannelHandlerContext channelHandlerContext) throws Exception {
		return null;
	}

	@Override
	protected boolean handleResponse(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
		return false;
	}
}
