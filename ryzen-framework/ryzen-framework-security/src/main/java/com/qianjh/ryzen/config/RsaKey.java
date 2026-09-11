package com.qianjh.ryzen.config;

import lombok.Data;

@Data
public class RsaKey {
    private String publicKey;
    private String privateKey;
    private Long expireTime;
}
