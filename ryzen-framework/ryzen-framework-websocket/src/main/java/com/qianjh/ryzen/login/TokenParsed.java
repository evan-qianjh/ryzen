package com.qianjh.ryzen.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 *
 * @author QianJH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TokenParsed {
    private Long accountId;

    private Map<String, String> extraHeaders;
}
