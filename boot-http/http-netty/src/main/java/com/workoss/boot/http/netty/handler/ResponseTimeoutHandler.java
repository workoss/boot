package com.workoss.boot.http.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class ResponseTimeoutHandler extends ChannelInboundHandlerAdapter {

	private final long timeoutMillis;

	private ScheduledFuture<?> responseTimeoutWatcher;

	public ResponseTimeoutHandler(long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		if (timeoutMillis<=0){
			return;
		}
		this.responseTimeoutWatcher = ctx.executor().schedule(() -> responseTimedOut(ctx), timeoutMillis,
				TimeUnit.MILLISECONDS);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		if (responseTimeoutWatcher != null && !responseTimeoutWatcher.isDone()) {
			responseTimeoutWatcher.cancel(false);
			responseTimeoutWatcher = null;
		}
	}

	private void responseTimedOut(ChannelHandlerContext ctx){
		ctx.fireExceptionCaught(new TimeoutException(String.format("Channel response timed out after %d milliseconds.",timeoutMillis)));
	}


}
