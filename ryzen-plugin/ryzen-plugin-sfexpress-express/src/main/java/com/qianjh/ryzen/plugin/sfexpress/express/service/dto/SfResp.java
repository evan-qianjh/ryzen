package com.qianjh.ryzen.plugin.sfexpress.express.service.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record SfResp<T>(
        Boolean success, // true 请求成功，false 请求失败
        String errorCode, // 错误编码，S0000成功
        String errorMsg, // 错误描述
        T msgData // 返回的详细数据
) {
}
