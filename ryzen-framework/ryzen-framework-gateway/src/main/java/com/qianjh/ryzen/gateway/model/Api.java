package com.qianjh.ryzen.gateway.model;

import lombok.*;

/**
 * @author QianJH
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Api {
    private String method;
    private String path;
}
