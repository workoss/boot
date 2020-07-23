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

/**
 * @author workoss
 */
public interface Promise<V> extends Future<V> {

	/**
	 * Marks this future as a success and notifies all listeners. If it is success or
	 * failed already it will throw an {@link IllegalStateException}.
	 * @param result
	 * @return
	 */
	Promise<V> setSuccess(V result);

	/**
	 * Marks this future as a success and notifies all listeners.
	 * @param result
	 * @return {@code true} if and only if successfully marked this future as a success.
	 * Otherwise {@code false} because this future is already marked as either a success
	 * or a failure.
	 */
	boolean trySuccess(V result);

	/**
	 * Marks this future as a failure and notifies all listeners. If it is success or
	 * failed already it will throw an {@link IllegalStateException}.
	 * @param cause
	 * @return
	 */
	Promise<V> setFailure(Throwable cause);

	/**
	 * Marks this future as a failure and notifies all listeners.
	 * @param cause
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
	 * @param listener
	 * @return
	 */
	@Override
	Promise<V> addListener(FutureListener<V> listener);

	/**
	 * addListeners
	 * @param listeners
	 * @return
	 */
	@Override
	Promise<V> addListeners(List<FutureListener<V>> listeners);

	/**
	 * removeListener
	 * @param listener
	 * @return
	 */
	@Override
	Promise<V> removeListener(FutureListener<V> listener);

	/**
	 * removeListeners
	 * @param listeners
	 * @return
	 */
	@Override
	Promise<V> removeListeners(List<FutureListener<V>> listeners);

	/**
	 * sync
	 * @return
	 * @throws InterruptedException
	 */
	@Override
	Promise<V> sync() throws InterruptedException;

	/**
	 * syncUninterruptibly
	 * @return
	 */
	@Override
	Promise<V> syncUninterruptibly();

	/**
	 * await
	 * @return
	 * @throws InterruptedException
	 */
	@Override
	Promise<V> await() throws InterruptedException;

	/**
	 * awaitUninterruptibly
	 * @return
	 */
	@Override
	Promise<V> awaitUninterruptibly();

}
