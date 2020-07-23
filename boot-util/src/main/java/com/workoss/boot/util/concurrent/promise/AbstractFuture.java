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
package com.workoss.boot.util.concurrent.promise;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author workoss
 */
public abstract class AbstractFuture<V> implements Future<V> {

	@Override
	public V get() throws InterruptedException, ExecutionException {
		/**
		 * 阻塞等到await()调用完成，即失败或返回结果
		 */
		await();
		/**
		 * 获取失败异常信息
		 */
		Throwable cause = cause();
		/**
		 * 如果异常信息为null，直接获取响应结果
		 */
		if (cause == null) {
			return getNow();
		}
		/**
		 * 如果返回结果result == CancellationException（即执行了cancel()），则抛出该异常
		 * 否则，抛出ExecutionException
		 */
		if (cause instanceof CancellationException) {
			throw (CancellationException) cause;
		}
		throw new ExecutionException(cause);
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		if (await(timeout, unit)) {
			Throwable cause = cause();
			if (cause == null) {
				return getNow();
			}
			if (cause instanceof CancellationException) {
				throw (CancellationException) cause;
			}
			throw new ExecutionException(cause);
		}
		/**
		 * 如果没有在指定的时间内await没有完成，抛出超时异常
		 */
		throw new TimeoutException();

	}

}
