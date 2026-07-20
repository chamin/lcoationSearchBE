package com.cham.demo.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LogManager.getLogger(RequestResponseLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request, 10240);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            log.info("Request  -> {} {} query=[{}] body=[{}]",
                    requestWrapper.getMethod(),
                    requestWrapper.getRequestURI(),
                    requestWrapper.getQueryString(),
                    new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8));

            log.info("Response <- {} {} status={} body=[{}]",
                    requestWrapper.getMethod(),
                    requestWrapper.getRequestURI(),
                    responseWrapper.getStatus(),
                    new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8));

            responseWrapper.copyBodyToResponse();
        }
    }
}
