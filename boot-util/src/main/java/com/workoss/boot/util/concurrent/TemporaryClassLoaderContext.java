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
package com.workoss.boot.util.concurrent;

/**
 * 多线程 类加载上下文
 *
 * @author workoss
 */
public final class TemporaryClassLoaderContext implements AutoCloseable {

	private final Thread thread;

	private final ClassLoader originalContextClassLoader;

	private TemporaryClassLoaderContext(Thread thread, ClassLoader originalContextClassLoader) {
		this.thread = thread;
		this.originalContextClassLoader = originalContextClassLoader;
	}

	public static TemporaryClassLoaderContext of(ClassLoader cl) {
		final Thread t = Thread.currentThread();
		final ClassLoader original = t.getContextClassLoader();

		t.setContextClassLoader(cl);

		return new TemporaryClassLoaderContext(t, original);
	}

	@Override
	public void close() throws Exception {
		thread.setContextClassLoader(originalContextClassLoader);
	}

}
