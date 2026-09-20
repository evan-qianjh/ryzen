package com.qianjh.ryzen.framework.websocket.netty;

import com.qianjh.ryzen.framework.websocket.service.LoginService;
import com.qianjh.ryzen.framework.websocket.topic.TopicManager;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientManagerInitializer {
    private final TopicManager topicManager;
    private final LoginService loginService;

    @PostConstruct
    public void init() {
        ClientManager.init(topicManager, loginService);
    }
}
