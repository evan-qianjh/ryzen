package com.qianjh.ryzen.stream.topic.user.account;

import com.qianjh.ryzen.framework.stream.producer.Producer;
import com.qianjh.ryzen.framework.stream.producer.StreamProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

@Component
@Configuration
@RequiredArgsConstructor
public class UserAccountTopic_StreamProducer implements StreamProducer {
    private final Producer producer;

    private final Sinks.Many<Message<String>> sinks =
            Sinks.many().multicast().onBackpressureBuffer();

    @Bean
    Supplier<Flux<Message<String>>> userAccountSink() {
        return sinks::asFlux;
    }

    @Override
    public Sinks.Many<Message<String>> getSinks() {
        return sinks;
    }

    @Override
    public Producer getProducer() {
        return producer;
    }
}
