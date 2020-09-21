package com.workoss.boot.util.plugin;

/**
 * 插件异常
 *
 * @author workoss
 */
public class PluginRuntimeException extends RuntimeException {

	public PluginRuntimeException(Throwable throwable) {
		super(throwable);
	}

	public PluginRuntimeException(String s) {
		super(s);
	}

	public PluginRuntimeException(String s, Throwable throwable) {
		super(s, throwable);
	}

}
