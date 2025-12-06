package com.example.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class RequestResponseLoggingFilter implements jakarta.servlet.Filter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

        try {
            chain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            logRequest(wrappedRequest);
            logResponse(wrappedResponse);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String payload = "";
        byte[] buf = request.getContentAsByteArray();
        if (buf.length > 0) {
            payload = new String(buf, 0, buf.length, StandardCharsets.UTF_8);
        }
        log.info("HTTP REQUEST {} {} headers={} body={}", request.getMethod(), request.getRequestURI(), request.getHeaderNames(), payload);
    }

    private void logResponse(ContentCachingResponseWrapper response) {
        String payload = "";
        byte[] buf = response.getContentAsByteArray();
        if (buf.length > 0) {
            payload = new String(buf, 0, buf.length, StandardCharsets.UTF_8);
        }
        log.info("HTTP RESPONSE status={} body={}", response.getStatus(), payload);
    }
}
