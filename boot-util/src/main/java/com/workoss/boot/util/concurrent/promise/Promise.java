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

import java.util.List;

/**
 * @author workoss
 */
public interface Promise<V> extends Future<V> {

	/**
	 * Marks this future as a success and notifies all listeners. If it is success or
	 * failed already it will throw an {@link IllegalStateException}.
	 * @param result 对象
	 * @return promise
	 */
	Promise<V> setSuccess(V result);

	/**
	 * Marks this future as a success and notifies all listeners.
	 * @param result 对象
	 * @return {@code true} if and only if successfully marked this future as a success.
	 * Otherwise {@code false} because this future is already marked as either a success
	 * or a failure.
	 */
	boolean trySuccess(V result);

	/**
	 * Marks this future as a failure and notifies all listeners. If it is success or
	 * failed already it will throw an {@link IllegalStateException}.
	 * @param cause 异常
	 * @return promise
	 */
	Promise<V> setFailure(Throwable cause);

	/**
	 * Marks this future as a failure and notifies all listeners.
	 * @param cause 异常
	 * @return {@code true} if and only if successfully marked this future as a failure.
	 * {@code false} because this future is already marked as either a success or a
	 * failure.
	 */
	boolean tryFailure(Throwable cause);

	/**
	 * Make this future impossible to cancel.
	 * @return {@code true} if and only if successfully marked this future as
	 * uncancellable or it is already done without being cancelled. {@code false} if this
	 * future has been cancelled already.
	 */
	boolean setUncancellable();

	/**
	 * addListener
	 * @param listener lis
	 * @return promise
	 */
	@Override
	Promise<V> addListener(FutureListener<V> listener);

	/**
	 * addListeners
	 * @param listeners lis
	 * @return promise
	 */
	@Override
	Promise<V> addListeners(List<FutureListener<V>> listeners);

	/**
	 * removeListener
	 * @param listener lis
	 * @return promise
	 */
	@Override
	Promise<V> removeListener(FutureListener<V> listener);

	/**
	 * removeListeners
	 * @param listeners lis
	 * @return promise
	 */
	@Override
	Promise<V> removeListeners(List<FutureListener<V>> listeners);

	/**
	 * sync
	 * @return promise
	 * @throws InterruptedException 异常
	 */
	@Override
	Promise<V> sync() throws InterruptedException;

	/**
	 * syncUninterruptibly
	 * @return promise
	 */
	@Override
	Promise<V> syncUninterruptibly();

	/**
	 * await
	 * @return promise
	 * @throws InterruptedException 异常
	 */
	@Override
	Promise<V> await() throws InterruptedException;

	/**
	 * awaitUninterruptibly
	 * @return promise
	 */
	@Override
	Promise<V> awaitUninterruptibly();

}
