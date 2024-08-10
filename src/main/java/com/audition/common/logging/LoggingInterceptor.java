package com.audition.common.logging;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

@Component
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(final HttpRequest request,
        final byte[] body,
        final ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        final ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(final HttpRequest request, final byte[] body) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Request URI: {}", request.getURI());
            LOG.info("Request Method: {}", request.getMethod());
            LOG.info("Request Headers: {}", request.getHeaders());
            LOG.info("Request Body: {}", new String(body, StandardCharsets.UTF_8));
        }
    }

    private void logResponse(final ClientHttpResponse response) throws IOException {
        if (LOG.isInfoEnabled()) {
            try (InputStream responseBody = response.getBody()) {
                final String responseBodyString = StreamUtils.copyToString(responseBody, StandardCharsets.UTF_8);
                LOG.info("Response Status Code: {}", response.getStatusCode());
                LOG.info("Response Headers: {}", response.getHeaders());
                LOG.info("Response Body: {}", responseBodyString);
            }
        }
    }
}
