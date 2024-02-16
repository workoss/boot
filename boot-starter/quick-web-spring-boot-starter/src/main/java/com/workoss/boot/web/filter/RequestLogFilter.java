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
import org.slf4j.event.Level;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.time.Instant;

/**
 * @author workoss
 */
@Slf4j
public class RequestLogFilter extends CommonsRequestLoggingFilter {

    private static final String COST_KEY = "_start_time";

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
        Level logLevel = logProperties.getLogLevel();
        if (logLevel.compareTo(Level.DEBUG) == 0) {
            log.atLevel(logLevel).log("[REQ] uri:{} method:{} start", request.getRequestURI(), request.getMethod());
        }else {
            log.atLevel(logLevel).log("[REQ] uri:{} method:{} start", request.getRequestURI(), request.getMethod());
        }
        request.setAttribute(COST_KEY, Instant.now().toEpochMilli());
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        // TODO 持久化
        Long startTime = (Long)request.getAttribute(COST_KEY);
        Long costTime = (startTime!=null?(Instant.now().toEpochMilli()-startTime):null);
        log.atLevel(logProperties.getLogLevel()).log("[RESP] uri:{} cost:{}ms", request.getRequestURI(),costTime);
    }

}
