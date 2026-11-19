package com.qianjh.ryzen.stream;

import com.qianjh.ryzen.framework.stream.consumer.StreamConsumer;
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
public class ImSchedulerMessage_StreamConsumer implements StreamConsumer {

    @Bean
    public Consumer<Message<String>> imsSchedulerMessageSource() {
        return message -> {
            MessageHeaders headers = message.getHeaders();

            log.info("message ::: {}", message);
        };
    }
}
