package com.workoss.boot.http;

import com.workoss.boot.annotation.lang.Nullable;
import com.workoss.boot.http.util.HttpCommonUtil;
import com.workoss.boot.util.Assert;

import java.time.Duration;

public class HttpClientResponseCookie extends HttpClientCookie {

	private final Duration maxAge;

	@Nullable
	private final String domain;

	@Nullable
	private final String path;

	private final boolean secure;

	private final boolean httpOnly;

	@Nullable
	private final String sameSite;

	private HttpClientResponseCookie(String name, String value, Duration maxAge, @Nullable String domain,
			@Nullable String path, boolean secure, boolean httpOnly, @Nullable String sameSite) {

		super(name, value);
		Assert.notNull(maxAge, "Max age must not be null");

		this.maxAge = maxAge;
		this.domain = domain;
		this.path = path;
		this.secure = secure;
		this.httpOnly = httpOnly;
		this.sameSite = sameSite;

		HttpCommonUtil.validateCookieName(name);
		HttpCommonUtil.validateCookieValue(value);
		HttpCommonUtil.validateDomain(domain);
		HttpCommonUtil.validatePath(path);
	}

}
