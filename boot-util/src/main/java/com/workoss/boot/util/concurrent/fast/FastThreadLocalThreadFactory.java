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
