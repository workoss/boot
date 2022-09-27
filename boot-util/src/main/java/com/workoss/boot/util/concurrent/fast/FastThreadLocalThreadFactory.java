/*
 * Copyright 2019-2022 workoss (https://www.workoss.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.workoss.boot.util.concurrent.fast;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author workoss
 */
public class FastThreadLocalThreadFactory implements ThreadFactory {

	/**
	 * 线程池的ID，所有 FastThreadLocalThreadFactory实例 共有
	 */
	private static AtomicInteger poolId = new AtomicInteger();

	/**
	 * 线程池中的线程ID，FastThreadLocalThreadFactory实例私有
	 */
	private AtomicInteger nextId = new AtomicInteger();

	/**
	 * 线程名称前缀
	 */
	private String prefix;

	/**
	 * 是否是后台线程
	 */
	private boolean deamon;

	/**
	 * 线程的优先级
	 */
	private int priority;

	/**
	 * 线程组
	 */
	private ThreadGroup threadGroup;

	public FastThreadLocalThreadFactory(String poolName) {
		this(poolName, Thread.NORM_PRIORITY, false, Thread.currentThread().getThreadGroup());
	}

	public FastThreadLocalThreadFactory(String poolName, int priority, boolean deamon, ThreadGroup threadGroup) {
		if (poolName == null) {
			throw new NullPointerException("poolName");
		}
		if (priority > Thread.MAX_PRIORITY || priority < Thread.MIN_PRIORITY) {
			throw new IllegalArgumentException("priority");
		}
		this.prefix = poolName + "-" + poolId.getAndIncrement() + "-";
		this.priority = priority;
		this.deamon = deamon;
		this.threadGroup = threadGroup;
	}

	@Override
	public Thread newThread(Runnable r) {
		// 线程名称 poolName-poolId-nextId
		Thread thread = new FastThreadLocalThread(r, threadGroup, prefix + nextId.incrementAndGet());
		if (thread.isDaemon() != deamon) {
			thread.setDaemon(deamon);
		}
		if (thread.getPriority() != priority) {
			thread.setPriority(priority);
		}
		return thread;
	}

}
