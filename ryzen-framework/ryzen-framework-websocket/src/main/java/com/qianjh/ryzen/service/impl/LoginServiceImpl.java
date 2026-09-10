package com.qianjh.ryzen.service.impl;

import com.qianjh.ryzen.exception.BusinessException;
import com.qianjh.ryzen.login.TokenParsed;
import com.qianjh.ryzen.login.TokenParser;
import com.qianjh.ryzen.login.TokenParserHolder;
import com.qianjh.ryzen.netty.ClientManager;
import com.qianjh.ryzen.service.LoginService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 *
 * @author QianJH 
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    @Override
    public void login(ChannelId clientId, Long tenantId, String token) {
        TokenParser tokenParser = TokenParserHolder.get();
        if (tokenParser == null) {
            log.error("未注册TokenParser");
            throw new IllegalStateException("未注册TokenParser");
        }

        TokenParsed parsed = tokenParser.parse(token);
        if (parsed == null) {
            throw new BusinessException("token error");
        }

        // 额外参数
        Map<String, String> extraHeaders = parsed.getExtraHeaders();
        if (extraHeaders != null) {
            extraHeaders.forEach((k, v) -> {
                ClientManager.setAttrVal(clientId, k, v);
            });
        }

        // 登录
        ClientManager.login(clientId, parsed.getAccountId());
    }

    @Override
    public void logout(Channel client) {
        ClientManager.logout(client);
    }
}
