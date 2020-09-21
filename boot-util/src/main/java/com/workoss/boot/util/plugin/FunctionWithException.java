package com.workoss.boot.util.plugin;

/**
 * 异常func
 *
 * @param <T> 入
 * @param <R> 返回
 * @param <E> 异常
 * @author workoss
 */
@FunctionalInterface
public interface FunctionWithException<T, R, E extends Throwable> {

	/**
	 * Calls this function.
	 * @param value The argument to the function.
	 * @return The result of thus supplier.
	 * @throws E This function may throw an exception.
	 */
	R apply(T value) throws E;

}
