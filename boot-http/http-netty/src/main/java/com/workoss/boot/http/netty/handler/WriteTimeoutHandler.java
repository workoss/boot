package com.workoss.boot.http.netty.handler;

import io.netty.channel.*;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class WriteTimeoutHandler extends ChannelOutboundHandlerAdapter {

	private final long timeoutMillis;

	public WriteTimeoutHandler(long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}

	private long lastWriteMillis;

	private long lastWriteProgress;

	private final ChannelFutureListener writeListener = (future) -> this.lastWriteMillis = System.currentTimeMillis();

	private ScheduledFuture<?> writeTimeoutWatcher;

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		ctx.write(msg, promise.unvoid()).addListener(writeListener);
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		if (timeoutMillis <= 0) {
			return;
		}
		this.writeTimeoutWatcher = ctx.executor().scheduleAtFixedRate(() -> writeTimeoutRunnable(ctx), timeoutMillis,
				timeoutMillis, TimeUnit.MILLISECONDS);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		if (writeTimeoutWatcher != null && !writeTimeoutWatcher.isDone()) {
			writeTimeoutWatcher.cancel(false);
			writeTimeoutWatcher = null;
		}
	}

	private void writeTimeoutRunnable(ChannelHandlerContext ctx) {
		if ((timeoutMillis - (System.currentTimeMillis() - lastWriteMillis)) > 0) {
			return;
		}
		ChannelOutboundBuffer buffer = ctx.channel().unsafe().outboundBuffer();
		if (buffer != null) {
			long writeProcess = buffer.currentProgress();
			if (writeProcess != lastWriteProgress) {
				this.lastWriteProgress = writeProcess;
				return;
			}
		}
		ctx.fireExceptionCaught(new TimeoutException(
				String.format("Channel write operation timed out after %d milliseconds.", timeoutMillis)));
	}

}
