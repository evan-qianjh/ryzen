package com.qianjh.ryzen.netty;

import com.qianjh.ryzen.service.LoginService;
import com.qianjh.ryzen.topic.TopicManager;
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
