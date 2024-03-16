/*
 * Copyright 2019-2024 workoss (https://www.workoss.com)
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
