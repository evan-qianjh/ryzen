package com.qianjh.ryzen.config;

import lombok.Data;

import java.util.Map;

@Data
public class RsaProperties {
    private volatile Long currentKeyId;
    private volatile Map<Long, RsaKey> keys;
}
