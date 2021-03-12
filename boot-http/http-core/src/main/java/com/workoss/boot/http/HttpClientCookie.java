package com.workoss.boot.http;

import com.workoss.boot.annotation.lang.Nullable;
import com.workoss.boot.util.Assert;

import java.net.HttpCookie;

public class HttpClientCookie {

	private final String name;

	private final String value;

	public HttpClientCookie(String name, @Nullable String value) {
		Assert.hasLength(name, "'name' is required and must not be empty.");
		this.name = name;
		this.value = (value != null ? value : "");
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	@Override
	public boolean equals(@Nullable Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof HttpCookie)) {
			return false;
		}
		HttpCookie otherCookie = (HttpCookie) other;
		return (this.name.equalsIgnoreCase(otherCookie.getName()));
	}

	@Override
	public String toString() {
		return this.name + '=' + this.value;
	}

}
