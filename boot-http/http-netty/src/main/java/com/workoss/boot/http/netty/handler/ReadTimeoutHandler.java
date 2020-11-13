package com.workoss.boot.http.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class ReadTimeoutHandler extends ChannelInboundHandlerAdapter {

	private final long timeoutMillis;

	private long lastReadMillis;

	private ScheduledFuture<?> readTimeoutWatcher;

	public ReadTimeoutHandler(long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		this.lastReadMillis = System.currentTimeMillis();
		ctx.fireChannelReadComplete();
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		if (timeoutMillis <= 0) {
			return;
		}
		this.readTimeoutWatcher = ctx.executor().scheduleAtFixedRate(() -> readTimeoutRunnable(ctx), timeoutMillis,
				timeoutMillis, TimeUnit.MILLISECONDS);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		if (readTimeoutWatcher != null && !readTimeoutWatcher.isDone()) {
			readTimeoutWatcher.cancel(false);
			readTimeoutWatcher = null;
		}
	}

	private void readTimeoutRunnable(ChannelHandlerContext ctx) {
		if ((timeoutMillis - (System.currentTimeMillis() - lastReadMillis)) > 0) {
			return;
		}
		ctx.fireExceptionCaught(new TimeoutException(
				String.format("Channel read timed out after %d milliseconds.", readTimeoutWatcher)));
	}

}
