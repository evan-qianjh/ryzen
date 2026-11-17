package com.qianjh.ryzen.login;

import com.qianjh.ryzen.framework.websocket.login.TokenParser;
import com.qianjh.ryzen.framework.websocket.login.TokenParserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenParserRegister implements InitializingBean {

    private final TokenParser tokenParser;

    @Override
    public void afterPropertiesSet() throws Exception {
        TokenParserHolder.register(tokenParser);
    }
}
