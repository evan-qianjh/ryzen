package com.qianjh.ryzen.framework.http.webmvc.service;

import com.qianjh.ryzen.framework.common.dto.ClientInfo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.TreeMap;

/**
 * @author QianJH
 */
public interface HttpRequestService {

    ClientInfo getClientInfo(HttpServletRequest request);

    String toJson(Object body);

    String getAppName(ClientInfo clientInfo);

    String getOsName(ClientInfo clientInfo);

    TreeMap<String, String> getHeaderMap(HttpServletRequest request);

    TreeMap<String, String> getParameterMap(HttpServletRequest request);
}
