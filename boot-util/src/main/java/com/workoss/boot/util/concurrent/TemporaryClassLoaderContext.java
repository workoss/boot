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
