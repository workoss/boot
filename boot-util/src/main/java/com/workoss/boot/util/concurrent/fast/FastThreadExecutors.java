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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FastThreadExecutors {

	/**
	 * 创建一个线程数固定（corePoolSize==maximumPoolSize）的线程池 核心线程会一直存在，不被回收 如果一个核心线程由于异常跪了，会新创建一个线程
	 * 无界队列LinkedBlockingQueue
	 */
	public static Executor newFixedFastThreadPool(int nThreads, String poolName) {
		return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
				new FastThreadLocalThreadFactory(poolName));
	}

	/**
	 * corePoolSize==0 maximumPoolSize==Integer.MAX_VALUE 队列：SynchronousQueue
	 * 创建一个线程池：当池中的线程都处于忙碌状态时，会立即新建一个线程来处理新来的任务 这种池将会在执行许多耗时短的异步任务的时候提高程序的性能
	 * 60秒钟内没有使用的线程将会被中止，并且从线程池中移除，因此几乎不必担心耗费资源
	 */
	public static Executor newCachedFastThreadPool(String poolName) {
		return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(),
				new FastThreadLocalThreadFactory(poolName));
	}

	/**
	 * 自定义各种参数
	 */
	public static Executor newLimitedFastThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime,
			TimeUnit unit, BlockingQueue<Runnable> workQueue, String poolName, RejectedExecutionHandler handler) {
		return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				new FastThreadLocalThreadFactory(poolName), handler);
	}

}
