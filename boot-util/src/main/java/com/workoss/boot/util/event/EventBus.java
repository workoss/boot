/*
 * The MIT License
 * Copyright © 2020-2021 workoss
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.workoss.boot.util.event;

import com.workoss.boot.util.collection.CollectionUtils;
import com.workoss.boot.util.concurrent.AsyncRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author workoss
 */
public class EventBus {

	private static final Logger log = LoggerFactory.getLogger(EventBus.class);

	private static final boolean EVENT_BUS_ENABLE = true;

	/**
	 * 某中事件的订阅者
	 */

	private final static ConcurrentMap<Class<? extends Event>, CopyOnWriteArraySet<Subscriber>> SUBSCRIBER_MAP = new ConcurrentHashMap<Class<? extends Event>, CopyOnWriteArraySet<Subscriber>>();

	/**
	 * 是否开启事件总线功能
	 * @return 是否开启事件总线功能
	 */
	public static boolean isEnable() {
		return EVENT_BUS_ENABLE;
	}

	/**
	 * 是否开启事件总线功能
	 * @param eventClass 事件类型
	 * @return 是否开启事件总线功能
	 */
	public static boolean isEnable(Class<? extends Event> eventClass) {
		return EVENT_BUS_ENABLE && SUBSCRIBER_MAP.get(eventClass) != null;
	}

	/**
	 * 注册一个订阅者
	 * @param eventClass 事件类型
	 * @param subscriber 订阅者
	 */
	public static void register(Class<? extends Event> eventClass, Subscriber subscriber) {
		CopyOnWriteArraySet<Subscriber> set = SUBSCRIBER_MAP.get(eventClass);
		if (set == null) {
			set = new CopyOnWriteArraySet<Subscriber>();
			CopyOnWriteArraySet<Subscriber> old = SUBSCRIBER_MAP.putIfAbsent(eventClass, set);
			if (old != null) {
				set = old;
			}
		}
		set.add(subscriber);
		if (log.isDebugEnabled()) {
			log.debug("Register subscriber1: {} of event: {}.", subscriber, eventClass);
		}
	}

	/**
	 * 反注册一个订阅者
	 * @param eventClass 事件类型
	 * @param subscriber 订阅者
	 */
	public static void unRegister(Class<? extends Event> eventClass, Subscriber subscriber) {
		CopyOnWriteArraySet<Subscriber> set = SUBSCRIBER_MAP.get(eventClass);
		if (set != null) {
			set.remove(subscriber);
			if (log.isDebugEnabled()) {
				log.debug("UnRegister subscriber1: {} of event: {}.", subscriber, eventClass);
			}
		}
	}

	/**
	 * 给事件总线中丢一个事件
	 * @param event 事件
	 */
	public static void post(final Event event) {
		if (!isEnable()) {
			return;
		}
		CopyOnWriteArraySet<Subscriber> subscribers = SUBSCRIBER_MAP.get(event.getClass());
		if (CollectionUtils.isEmpty(subscribers)) {
			return;
		}

		for (final Subscriber subscriber : subscribers) {
			if (subscriber instanceof AsyncSubscriber) {
				final ThreadPoolExecutor asyncThreadPool = AsyncRuntime.getAsyncThreadPool();
				try {
					asyncThreadPool.execute(() -> handleEvent(subscriber, event));
				}
				catch (RejectedExecutionException e) {
					log.warn("This queue is full when post event to async execute, queue size is "
							+ asyncThreadPool.getQueue().size()
							+ ", please optimize this async thread pool of eventbus.");
					subscriber.onError(e);
				}
			}
			else { // 异步
				handleEvent(subscriber, event);
			}
		}

	}

	private static void handleEvent(final Subscriber subscriber, final Event event) {
		try {
			subscriber.onEvent(event);
		}
		catch (Throwable e) {
			log.warn("Handle " + event.getClass() + " error", e);
			subscriber.onError(e);
		}
	}

}
