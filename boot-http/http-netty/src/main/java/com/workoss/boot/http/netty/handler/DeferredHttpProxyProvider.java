package com.workoss.boot.http.netty.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import reactor.netty.ConnectionObserver;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 延迟向通道管道提供代理处理程序。
 *
 * @author workoss
 */
public class DeferredHttpProxyProvider implements Function<Bootstrap, BiConsumer<ConnectionObserver, Channel>> {

	@Override
	public BiConsumer<ConnectionObserver, Channel> apply(Bootstrap bootstrap) {
		return null;
	}

}
