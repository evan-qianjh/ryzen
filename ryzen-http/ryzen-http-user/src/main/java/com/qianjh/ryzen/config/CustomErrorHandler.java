package com.qianjh.ryzen.config;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

/**
 * @author QianJH
 */
public class CustomErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        // 不让 RestTemplate 认为 4xx/5xx 是错误
        return false;
    }
}
