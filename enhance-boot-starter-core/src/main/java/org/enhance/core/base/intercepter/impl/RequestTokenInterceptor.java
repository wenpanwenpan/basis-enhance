package org.enhance.core.base.intercepter.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.enhance.core.base.intercepter.EventRequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.security.Signer;

/**
 * <p>
 * 为RestTemplate加上oauth token请求头
 * </p>
 *
 * @author wenpan 2022/9/25 12:02 下午
 */
public class RequestTokenInterceptor implements EventRequestInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestTokenInterceptor.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String OAUTH_TOKEN_PREFIX = "Bearer ";
    private Signer signer;

    @NonNull
    @Override
    public ClientHttpResponse intercept(HttpRequest request, @NonNull byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        // 获取请求头
        HttpHeaders headers = request.getHeaders();
        setLabel(headers);
        return execution.execute(request, body);
    }

    private void setLabel(HttpHeaders headers) {

        // 请求头里添加token

    }
}