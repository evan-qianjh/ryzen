package com.qianjh.ryzen.job;


import com.qianjh.ryzen.framework.StreamOutbox;
import com.qianjh.ryzen.framework.stream.model.StreamHeader;
import com.qianjh.ryzen.service.ApplicationService;
import com.qianjh.ryzen.service.StreamOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublishJob {

    private final StreamOutboxService streamOutboxService;
    private final ApplicationService applicationService;
    private final StreamBridge streamBridge;


    @Scheduled(fixedDelay = 1000)
    public void publish() {
        String name = applicationService.getName();
        String instance = applicationService.getInstance();

        // lock
        List<StreamOutbox> entities = streamOutboxService.claims();
        if (entities.isEmpty()) {
            return;
        }

        // 依次处理
        for (StreamOutbox entity : entities) {
            // 发送
            Message<String> message = buildMessage(entity);

            boolean success = streamBridge.send(entity.getTopic(), message);

            if (success) {
                log.info("OutboxEvent发送成功 ::: id={}", entity.getId());
                // 删除
                streamOutboxService.remove(entity);
            } else {
                log.error("OutboxEvent发送失败 ::: id={}", entity.getId());
                // 重试
                streamOutboxService.retry(entity);
            }
        }
    }

    /**
     * 构建消息
     *
     * @param stream 消息流
     * @return 消息
     */
    Message<String> buildMessage(StreamOutbox stream) {
        Message<String> message = MessageBuilder
                .withPayload(stream.getPayload())
                // kafka headers
                .setHeader(KafkaHeaders.KEY, stream.getShardingKey())
                // custom headers
                .setHeader(StreamHeader.ID, stream.getId())
                .setHeader(StreamHeader.DOMAIN, stream.getDomain())
                .setHeader(StreamHeader.TYPE, stream.getType())
                .setHeader(StreamHeader.TIME, System.currentTimeMillis())
                .setHeader(StreamHeader.OEM, stream.getOemId())
                .setHeader(StreamHeader.PRODUCER, stream.getProducer())
                .setHeader(StreamHeader.PRODUCER_INSTANCE, stream.getProducerInstance())
                .build();
        log.debug("buildMessage ::: {}", message);
        return message;
    }

}
