package com.qianjh.ryzen.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author QianJH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ClientInfo {

    private String clientIp;

    private String clientCode;

    private String clientDevice;

    private String clientOs;

    private String clientHost;

    private String clientApp;

}
