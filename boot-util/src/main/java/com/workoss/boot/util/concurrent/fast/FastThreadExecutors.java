/*
 * Copyright © 2020-2021 workoss (workoss@icloud.com)
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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author workoss
 */
public class FastThreadExecutors {

	/**
	 * 创建一个线程数固定（corePoolSize==maximumPoolSize）的线程池 核心线程会一直存在，不被回收 如果一个核心线程由于异常跪了，会新创建一个线程
	 * 无界队列LinkedBlockingQueue
	 * @param nThreads 线程数
	 * @param poolName 连接池名称
	 * @return executor
	 */
	public static Executor newFixedFastThreadPool(int nThreads, String poolName) {
		return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
				new FastThreadLocalThreadFactory(poolName));
	}

	/**
	 * corePoolSize==0 maximumPoolSize==Integer.MAX_VALUE 队列：SynchronousQueue
	 * 创建一个线程池：当池中的线程都处于忙碌状态时，会立即新建一个线程来处理新来的任务 这种池将会在执行许多耗时短的异步任务的时候提高程序的性能
	 * 60秒钟内没有使用的线程将会被中止，并且从线程池中移除，因此几乎不必担心耗费资源
	 * @param poolName 连接池名称
	 * @return executor
	 */
	public static Executor newCachedFastThreadPool(String poolName) {
		return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(),
				new FastThreadLocalThreadFactory(poolName));
	}

	public static Executor newLimitedFastThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime,
			TimeUnit unit, BlockingQueue<Runnable> workQueue, String poolName, RejectedExecutionHandler handler) {
		return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				new FastThreadLocalThreadFactory(poolName), handler);
	}

}
