package com.workoss.boot.http.netty.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.proxy.ProxyConnectException;

import javax.net.ssl.SSLException;

/**
 * This class handles removing {@link SSLException SSLExceptions} from being propagated
 * when connecting to the proxy
 *
 * @author workoss
 */
public final class HttpProxyExceptionHandler extends ChannelDuplexHandler {

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (cause instanceof SSLException) {
			SSLException sslException = (SSLException) cause;
			if (sslException.getCause() instanceof ProxyConnectException) {
				ctx.fireExceptionCaught(sslException.getCause());
				return;
			}
		}
		ctx.fireExceptionCaught(cause);
	}

}
