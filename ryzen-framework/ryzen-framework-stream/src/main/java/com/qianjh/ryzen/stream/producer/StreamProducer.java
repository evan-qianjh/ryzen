package com.qianjh.ryzen.stream.producer;

import com.qianjh.ryzen.stream.model.StreamHeader;
import com.qianjh.ryzen.stream.model.StreamPayload;
import com.qianjh.ryzen.stream.util.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import reactor.core.publisher.Sinks;

import java.util.UUID;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

/**
 * @author QianJH
 */
public interface StreamProducer {
    default Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }

    default Message<String> buildMessage(Object payload, String domain, String type, Long tenantId, Producer producer) {
        Message<String> message = MessageBuilder
                .withPayload(StreamUtils.toJson(payload))
                .setHeader(StreamHeader.ID, UUID.randomUUID().toString())
                .setHeader(StreamHeader.DOMAIN, domain)
                .setHeader(StreamHeader.TYPE, type)
                .setHeader(StreamHeader.TIME, System.currentTimeMillis())
                .setHeader(StreamHeader.TENANT, tenantId)
                .setHeader(StreamHeader.PRODUCER, producer.getName())
                .setHeader(StreamHeader.PRODUCER_INSTANCE, producer.getInstance())
                .build();
        log().debug("buildMessage ::: {}", message);
        return message;
    }

    default void send(Message<String> message, Consumer<Message<String>> consumer) {
        // 事务提交后发送
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    consumer.accept(message);
                }
            });
        } else {
            consumer.accept(message);
        }
    }

    default void publish(StreamPayload<?> payload, Long tenantId) {
        // build
        Message<String> message = buildMessage(payload, payload.getDomain(), payload.getType(), tenantId, getProducer());

        // send
        send(message, x -> {
            while (getSinks().tryEmitNext(x).isFailure()) {
                LockSupport.parkNanos(100);
            }
        });
    }

    Sinks.Many<Message<String>> getSinks();

    Producer getProducer();
}
