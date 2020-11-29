package com.example.vtkdemo.logging;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccessLogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        //init configuration
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        long startTime = System.currentTimeMillis();
        chain.doFilter(request, response);
        long responseTime = System.currentTimeMillis() - startTime;

        if (LoggingContext.shouldLogAccess()) {
            int status = ((HttpServletResponse) response).getStatus();
            String method = ((HttpServletRequest) request).getMethod();
            String path = ((HttpServletRequest) request).getRequestURI();
            String query = ((HttpServletRequest) request).getQueryString();
            String pathWithQuery = query == null ? path : path + "?" + query;

            log.info("controller={} eventName={} path={} method={} status={} success={} responseTime={} logType=access",
                    LoggingContext.getControllerName(), LoggingContext.getControllerMethodName(), pathWithQuery, method, status, isSuccess(status),
                    responseTime);
            LoggingContext.clear();
        }
    }

    private boolean isSuccess(int status) {
        return status < HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    @Override
    public void destroy() {
// This method has no logic
    }
}
