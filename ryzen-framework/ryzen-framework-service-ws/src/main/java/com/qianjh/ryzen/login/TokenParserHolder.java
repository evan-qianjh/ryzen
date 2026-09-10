package com.qianjh.ryzen.login;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author QianJH
 */
@Slf4j
public class TokenParserHolder {
    private static TokenParser parser;

    public static void register(TokenParser impl) {
        parser = impl;
        log.info("TokenParser注册成功");
    }

    public static TokenParser get() {
        if (parser == null) {
            log.error("TokenParser未注册");
//            throw new IllegalStateException("TokenParserProvider not registered");
        }
        return parser;
    }
}
