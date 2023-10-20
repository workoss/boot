/*
 * Copyright 2019-2023 workoss (https://www.workoss.com)
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
package com.workoss.boot.web.filter;

import com.workoss.boot.autoconfigure.QuickWebProjectProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * @author workoss
 */
@Slf4j
public class RequestLogFilter extends CommonsRequestLoggingFilter {

	private final QuickWebProjectProperties.RequestProperties.LogProperties logProperties;

	public RequestLogFilter(QuickWebProjectProperties quickWebProjectProperties) {
		this.logProperties = quickWebProjectProperties.getRequest().getLog();
	}

	@Override
	protected boolean shouldLog(HttpServletRequest request) {
		return logProperties.getEnabled() != null ? logProperties.getEnabled() : Boolean.FALSE;
	}

	@Override
	protected void beforeRequest(HttpServletRequest request, String message) {
		// TODO 打印日志
		log.atLevel(logProperties.getLogLevel()).log("[REQ] uri:{}", request.getRequestURI());
		super.beforeRequest(request, message);
	}

	@Override
	protected void afterRequest(HttpServletRequest request, String message) {
		// TODO 持久化
		log.atLevel(logProperties.getLogLevel()).log("[RESP] uri:{} resp:{}", request.getRequestURI(), message);
		super.afterRequest(request, message);
	}

}
