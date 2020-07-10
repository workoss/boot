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

import com.workoss.boot.util.ObjectUtil;

public class FastThreadLocalRunnable implements Runnable {

	private Runnable runnable;

	public FastThreadLocalRunnable(Runnable runnable) {
		this.runnable = ObjectUtil.checkNotNull(runnable, "runnable");
	}

	public static Runnable wrap(Runnable runnable) {
		return runnable instanceof FastThreadLocalRunnable ? runnable : new FastThreadLocalRunnable(runnable);
	}

	@Override
	public void run() {
		try {
			// 运行任务
			this.runnable.run();
		}
		finally {
			/**
			 * 线程池中的线程由于会被复用，所以线程池中的每一条线程在执行task结束后，要清理掉其InternalThreadLocalMap和其内的FastThreadLocal信息，
			 * 否则，当这条线程在下一次被复用的时候，其ThreadLocalMap信息还存储着上一次被使用的时的信息；
			 * 另外，假设这条线程不再被使用，但是这个线程有可能不会被销毁（与线程池的类型和配置相关），那么其上的ThreadLocal将发生了资源泄露。
			 */
			FastThreadLocal.removeAll();
		}
	}

}
