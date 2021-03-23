package com.workoss.boot.plugin.mybatis.context;

import com.workoss.boot.util.context.MapContext;

import java.util.StringJoiner;

@SuppressWarnings("ALL")
public class SqlContext extends MapContext {

	public Object getInput(String key) {
		return this.get(getInputKey(key));
	}

	public void putInput(String key, Object value) {
		this.put(getInputKey(key), value);
	}

	public Object getOutput(String key) {
		return this.get(getOutputKey(key));
	}

	public Object getOutputOrInput(String key) {
		return this.getOrDefault(getOutputKey(key), getInput(key));
	}

	public void putOutput(String key, Object value) {
		this.put(getOutputKey(key), value);
	}

	private String getInputKey(String key) {
		return new StringJoiner("_").add("IN").add(key).toString();
	}

	private String getOutputKey(String key) {
		return new StringJoiner("_").add("OUT").add(key).toString();
	}

}
