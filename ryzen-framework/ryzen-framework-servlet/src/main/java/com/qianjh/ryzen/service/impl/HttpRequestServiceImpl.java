package com.qianjh.ryzen.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qianjh.ryzen.api.ClientInfo;
import com.qianjh.ryzen.header.ClientHeader;
import com.qianjh.ryzen.header.GatewayHeader;
import com.qianjh.ryzen.service.HttpRequestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author QianJH
 */
@Slf4j
@Service
public class HttpRequestServiceImpl implements HttpRequestService {
    private final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .create();

    @Override
    public ClientInfo getClientInfo(HttpServletRequest request) {
        String clientIp = request.getHeader(GatewayHeader.CLIENT_IP);
        String clientCode = request.getHeader(ClientHeader.CLIENT_CODE);
        String clientDevice = request.getHeader(ClientHeader.CLIENT_DEVICE);
        String clientOs = request.getHeader(ClientHeader.CLIENT_OS);
        String clientApp = request.getHeader(ClientHeader.CLIENT_APP);
        String clientHost = request.getHeader(ClientHeader.CLIENT_HOST);

        ClientInfo build = ClientInfo.builder()
                .clientIp(clientIp)
                .clientCode(clientCode)
                .clientDevice(clientDevice)
                .clientOs(clientOs)
                .clientHost(clientHost)
                .clientApp(clientApp)
                .build();
        log.debug("ClientInfo ::: path={}, {}", request.getServletPath(), build);
        return build;
    }

    @Override
    public String getHeaderJson(HttpServletRequest request) {
        Map<String, String> headers = getHeaderMap(request);
        return GSON.toJson(headers);
    }

    @Override
    public String getHeaderJson(HttpHeaders httpHeaders) {
        return GSON.toJson(httpHeaders);
    }

    @Override
    public String toJson(Object body) {
        if (body == null) {
            return null;
        }
        return GSON.toJson(body);
    }

    @Override
    public String getAppName(ClientInfo clientInfo) {
        String clientApp = clientInfo.getClientApp();
        if (StringUtils.isBlank(clientApp)) {
            return null;
        }
        return clientApp.split(",")[0];
    }

    @Override
    public String getOsName(ClientInfo clientInfo) {
        String clientOs = clientInfo.getClientOs();
        if (StringUtils.isBlank(clientOs)) {
            return null;
        }
        return clientOs.split(",")[0];
    }

    @Override
    public TreeMap<String, String> getHeaderMap(HttpServletRequest request) {
        TreeMap<String, String> headers = new TreeMap<>();
        Iterator<String> headerIterator = request.getHeaderNames().asIterator();
        while (headerIterator.hasNext()) {
            String key = headerIterator.next();
            String val = request.getHeader(key);
            headers.put(key, val);
        }
        return headers;
    }

    @Override
    public TreeMap<String, String> getParameterMap(HttpServletRequest request) {
        TreeMap<String, String> params = new TreeMap<>();
        for (String key : request.getParameterMap().keySet()) {
            params.put(key, request.getParameter(key));
        }
        return params;
    }

}
