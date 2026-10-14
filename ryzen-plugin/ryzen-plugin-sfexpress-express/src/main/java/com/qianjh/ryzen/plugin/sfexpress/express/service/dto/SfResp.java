package com.qianjh.ryzen.plugin.sfexpress.express.service.dto;

public record SfResp<T> (
        String success, // true 请求成功，false 请求失败
        String errorCode, // 错误编码，S0000成功
        String errorMsg, // 错误描述
        T msgData // 返回的详细数据
) {
}
