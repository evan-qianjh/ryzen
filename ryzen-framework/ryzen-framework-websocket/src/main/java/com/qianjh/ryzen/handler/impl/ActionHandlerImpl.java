package com.qianjh.ryzen.handler.impl;

import com.qianjh.ryzen.handler.ActionHandler;
import com.qianjh.ryzen.message.Action;
import com.qianjh.ryzen.message.RequestMessage;
import com.qianjh.ryzen.netty.handler.MessageHandler;
import com.qianjh.ryzen.service.LoginService;
import com.qianjh.ryzen.topic.TopicManager;
import com.qianjh.ryzen.util.SpringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ActionHandlerImpl implements ActionHandler {

    private final TopicManager topicManager;

    @Override
    public void handle(Channel client, Long tenantId, RequestMessage request) {
        ChannelId clientId = client.id();
        Action action = request.getAction();
        LoginService loginService = SpringUtils.getApplicationContext().getBean(LoginService.class);

        // 订阅
        if (Action.subscribe == action) {
            topicManager.subscribe(tenantId, clientId, request.getParams());
        }
        // 取消订阅
        else if (Action.unsubscribe == action) {
            topicManager.unsubscribe(tenantId, clientId, request.getParams());
        }
        // LOGIN
        else if (Action.login == action) {
            Assert.isTrue(Objects.nonNull(request.getParams()) && !request.getParams().isEmpty(), "required token");
            loginService.login(clientId, tenantId, request.getParams().getFirst());
        }
        // LOGOUT
        else if (Action.logout == action) {
            loginService.logout(client);
        }
        // more...

        // no else

        // 响应成功
        MessageHandler.respSuccess(clientId, request, null);
    }
}
