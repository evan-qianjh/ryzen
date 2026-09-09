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
public class HttpInfo {
    private String method;
    private String url;

    private String headers;
    private String params;
    private String body;
}
