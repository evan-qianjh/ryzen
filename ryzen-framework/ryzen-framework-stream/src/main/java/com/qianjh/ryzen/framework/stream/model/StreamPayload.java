package com.qianjh.ryzen.framework.stream.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author QianJH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class StreamPayload<BODY> {
    /**
     * 领域，例如 user
     */
    private String domain;

    /**
     * 类型，例如 user_created
     */
    private String type;

    /**
     * 报文体
     */
    private BODY body;
}
