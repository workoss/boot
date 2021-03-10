/*
 * Copyright © 2020-2021 workoss (WORKOSS)
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
