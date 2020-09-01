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

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author workoss
 */
@SuppressWarnings("ALL")
public interface Future<V> extends java.util.concurrent.Future<V> {

	/**
	 * Returns {@code true} if and only if the I/O operation was completed successfully.
	 *
	 * @return true/false
	 */
	boolean isSuccess();

	/**
	 * returns {@code true} if and only if the operation can be cancelled via
	 * {@link #cancel(boolean)}.
	 *
	 * @return true/false
	 */
	boolean isCancellable();

	/**
	 * Returns the cause of the failed I/O operation if the I/O operation has failed.
	 *
	 * @return 异常
	 */
	Throwable cause();

	/**
	 * Adds the specified listener to this future. The specified listener is notified when
	 * this future is {@linkplain #isDone() done}. If this future is already completed,
	 * the specified listener is notified immediately.
	 *
	 * @param listener listener
	 * @return future
	 */
	Future<V> addListener(FutureListener<V> listener);

	/**
	 * Adds the specified listeners to this future. The specified listeners is notified
	 * when this future is {@linkplain #isDone() done}. If this future is already
	 * completed, the specified listeners is notified immediately.
	 *
	 * @param listeners listener
	 * @return future
	 */
	Future<V> addListeners(List<FutureListener<V>> listeners);

	/**
	 * Removes the first occurrence of the specified listener from this future. The
	 * specified listener is no longer notified when this future is {@linkplain #isDone()
	 * done}. If the specified listener is not associated with this future, this method
	 * does nothing and returns silently.
	 *
	 * @param listener listener
	 * @return future
	 */
	Future<V> removeListener(FutureListener<V> listener);

	/**
	 * Removes the first occurrence for each of the listeners from this future. The
	 * specified listeners is no longer notified when this future is {@linkplain #isDone()
	 * done}. If the specified listeners is not associated with this future, this method
	 * does nothing and returns silently.
	 *
	 * @param listeners listener
	 * @return future
	 */
	Future<V> removeListeners(List<FutureListener<V>> listeners);

	/**
	 * Waits for this future until it is done, and rethrows the cause of the failure if
	 * this future failed.
	 *
	 * @return future
	 * @throws InterruptedException if the current thread was interrupted
	 */
	Future<V> sync() throws InterruptedException;

	/**
	 * Waits for this future until it is done, and rethrows the cause of the failure if
	 * this future failed. This method catches an {@link InterruptedException} and
	 * discards it silently.
	 *
	 * @return future
	 */
	Future<V> syncUninterruptibly();

	/**
	 * Waits for this future to be completed.
	 *
	 * @return future
	 * @throws InterruptedException if the current thread was interrupted
	 */
	Future<V> await() throws InterruptedException;

	/**
	 * Waits for this future to be completed without interruption. This method catches an
	 * {@link InterruptedException} and discards it silently.
	 *
	 * @return future
	 */
	Future<V> awaitUninterruptibly();

	/**
	 * Waits for this future to be completed within the specified time limit.
	 *
	 * @param timeout 超时时间
	 * @param timeUnit 单位
	 * @return {@code true} if and only if the future was completed within the specified
	 * time limit
	 * @throws InterruptedException if the current thread was interrupted
	 */
	boolean await(long timeout, TimeUnit timeUnit) throws InterruptedException;

	/**
	 * Waits for this future to be completed within the specified time limit.
	 *
	 * @param timeout 超时时间
	 * @param timeUnit 单位
	 * @return {@code true} if and only if the future was completed within the specified
	 * time limit without interruption. This method catches an
	 * {@link InterruptedException} and discards it silently.
	 */
	boolean awaitUninterruptibly(long timeout, TimeUnit timeUnit);

	/**
	 * Waits for this future to be completed within the specified time limit.
	 *
	 * @param timeoutMillis 超时毫秒
	 * @return {@code true} if and only if the future was completed within the specified
	 * time limit
	 * @throws InterruptedException if the current thread was interrupted
	 */
	boolean await(long timeoutMillis) throws InterruptedException;

	/**
	 * Waits for this future to be completed within the specified time limit.
	 *
	 * @param timeoutMillis 超时毫秒
	 * @return true/false
	 */
	boolean awaitUninterruptibly(long timeoutMillis);

	/**
	 * Return the result without blocking. If the future is not done yet this will return
	 * {@code null}.
	 * <p>
	 * As it is possible that a {@code null} value is used to mark the future as
	 * successful you also need to check if the future is really done with
	 * {@link #isDone()} and not relay on the returned {@code null} value.
	 *
	 * @return 对象
	 */
	V getNow();

	/**
	 * {@inheritDoc}
	 * <p>
	 * If the cancellation was successful it will fail the future with an
	 * {@link java.util.concurrent.CancellationException}.
	 *
	 * @param mayInterruptIfRunning mayInterruptIfRunning
	 * @return true/false
	 */
	@Override
	boolean cancel(boolean mayInterruptIfRunning);

}
