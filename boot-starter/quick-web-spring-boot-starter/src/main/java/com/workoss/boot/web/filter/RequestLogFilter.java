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
        //TODO 打印日志
        log.atLevel(logProperties.getLogLevel()).log("[REQ] uri:{}", request.getRequestURI());
        super.beforeRequest(request, message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        //TODO 持久化
        log.atLevel(logProperties.getLogLevel()).log("[RESP] uri:{} resp:{}", request.getRequestURI(), message);
        super.afterRequest(request, message);
    }
}
