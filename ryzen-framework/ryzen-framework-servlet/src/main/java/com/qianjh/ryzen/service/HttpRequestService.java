package com.qianjh.ryzen.service;

import com.qianjh.ryzen.api.ClientInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

import java.util.TreeMap;

/**
 * @author QianJH
 */
public interface HttpRequestService {

    ClientInfo getClientInfo(HttpServletRequest request);

    @Deprecated
    String getHeaderJson(HttpServletRequest request);

    @Deprecated
    String getHeaderJson(HttpHeaders httpHeaders);

    String toJson(Object body);

    String getAppName(ClientInfo clientInfo);

    String getOsName(ClientInfo clientInfo);

    TreeMap<String, String> getHeaderMap(HttpServletRequest request);

    TreeMap<String, String> getParameterMap(HttpServletRequest request);
}
