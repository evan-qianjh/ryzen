package com.qianjh.ryzen.stream.topic.user.account;

import com.qianjh.ryzen.framework.stream.consumer.StreamConsumer;
import com.qianjh.ryzen.framework.stream.subscriber.StreamSubscriberDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
@Configuration
@RequiredArgsConstructor
public class UserAccountTopic_StreamConsumer implements StreamConsumer {

    private final StreamSubscriberDispatcher streamSubscriberDispatcher;

    @Bean
    public Consumer<Message<String>> userAccountSource() {
        return message -> {
            MessageHeaders headers = message.getHeaders();
            String domain = getDomain(headers);
            String type = getType(headers);
            String payload = message.getPayload();

            streamSubscriberDispatcher.dispatch(domain, type, payload);
        };
    }
}
