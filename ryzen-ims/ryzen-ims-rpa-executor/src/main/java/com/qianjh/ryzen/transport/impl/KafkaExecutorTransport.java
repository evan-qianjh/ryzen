package com.qianjh.ryzen.transport.impl;

import com.qianjh.ryzen.config.ExecutorProperties;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.stream.model.StreamHeader;
import com.qianjh.ryzen.framework.stream.model.StreamPayload;
import com.qianjh.ryzen.framework.stream.producer.Producer;
import com.qianjh.ryzen.framework.stream.util.StreamUtils;
import com.qianjh.ryzen.ims.dto.ExecutorMessage;
import com.qianjh.ryzen.ims.dto.ExecutorSnapshot;
import com.qianjh.ryzen.ims.enums.ExecutorToSchedulerReportEventType;
import com.qianjh.ryzen.ims.stream.executor_to_scheduler.report.*;
import com.qianjh.ryzen.transport.ExecutorTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * kafka实现的执行器通信层
 *
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaExecutorTransport implements ExecutorTransport {
    private final Producer producer;
    private final ExecutorProperties properties;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void reportRegister(ExecutorSnapshot snapshot) {
        ExecutorToSchedulerReportRegister.Body body = ExecutorToSchedulerReportRegister.Body.builder()
                .executorCode(snapshot.executorCode())
                .build();

        StreamPayload<ExecutorToSchedulerReportRegister.Body> payload = StreamPayload.<ExecutorToSchedulerReportRegister.Body>builder()
                .domain(ExecutorToSchedulerReportRegister.DOMAIN)
                .type(ExecutorToSchedulerReportRegister.TYPE)
                .body(body)
                .build();

        send(body.executorCode(), payload);
    }

    @Override
    public void reportHeartbeat(ExecutorSnapshot snapshot) {
        ExecutorToSchedulerReportHeartbeat.Body body = ExecutorToSchedulerReportHeartbeat.Body.builder()
                .executorCode(snapshot.executorCode())
                .assignmentId(snapshot.assignmentId())
                .status(snapshot.status())
                .currentOperation(snapshot.currentOperation())
                .currentTaskId(snapshot.currentTaskId())
                .errorMessage(snapshot.errorMessage())
                .build();

        StreamPayload<ExecutorToSchedulerReportHeartbeat.Body> payload = StreamPayload.<ExecutorToSchedulerReportHeartbeat.Body>builder()
                .domain(ExecutorToSchedulerReportHeartbeat.DOMAIN)
                .type(ExecutorToSchedulerReportHeartbeat.TYPE)
                .body(body)
                .build();

        send(body.executorCode(), payload);
    }

    @Override
    public void reportEvent(ExecutorSnapshot snapshot, ExecutorToSchedulerReportEventType type) {
        ExecutorToSchedulerReportEvent.Body body = ExecutorToSchedulerReportEvent.Body.builder()
                .executorCode(snapshot.executorCode())
                .assignmentId(snapshot.assignmentId())
                .type(type)
                .build();

        StreamPayload<ExecutorToSchedulerReportEvent.Body> payload = StreamPayload.<ExecutorToSchedulerReportEvent.Body>builder()
                .domain(ExecutorToSchedulerReportEvent.DOMAIN)
                .type(ExecutorToSchedulerReportEvent.TYPE)
                .body(body)
                .build();

        send(body.executorCode(), payload);
    }

    @Override
    public void reportMessage(ExecutorMessage message) {
        ExecutorToSchedulerReportMessage.Body body = ExecutorToSchedulerReportMessage.Body.builder()
                .imType(message.imType())
                .direction(message.direction())
                .executorCode(message.executorCode())
                .accountCode(message.accountCode())
                .messageCode(message.messageCode())
                .conversationCode(message.conversationCode())
                .conversationTitle(message.conversationTitle())
                .conversationSubTitle(message.conversationSubTitle())
                .conversationAvatar(message.conversationAvatar())
                .senderCode(message.senderCode())
                .senderName(message.senderName())
                .senderAvatar(message.senderAvatar())
                .sentTime(message.sentTime())
                .content(message.content())
                .contentType(message.contentType())
                .build();

        StreamPayload<ExecutorToSchedulerReportMessage.Body> payload = StreamPayload.<ExecutorToSchedulerReportMessage.Body>builder()
                .domain(ExecutorToSchedulerReportMessage.DOMAIN)
                .type(ExecutorToSchedulerReportMessage.TYPE)
                .body(body)
                .build();

        send(body.executorCode(), payload);
    }

    @Override
    public void commandResponse(ExecutorToSchedulerCommandResponse.Body body) {
        StreamPayload<ExecutorToSchedulerCommandResponse.Body> payload = StreamPayload.<ExecutorToSchedulerCommandResponse.Body>builder()
                .domain(ExecutorToSchedulerCommandResponse.DOMAIN)
                .type(ExecutorToSchedulerCommandResponse.TYPE)
                .body(body)
                .build();

        send(body.executorCode(), payload);
    }

    @Override
    public void taskResponse(ExecutorToSchedulerTaskResponse.Body body) {
        StreamPayload<ExecutorToSchedulerTaskResponse.Body> payload = StreamPayload.<ExecutorToSchedulerTaskResponse.Body>builder()
                .domain(ExecutorToSchedulerTaskResponse.DOMAIN)
                .type(ExecutorToSchedulerTaskResponse.TYPE)
                .body(body)
                .build();
        send(body.executorCode(), payload);
    }

    /**
     * 发送
     *
     * @param key     路由key
     * @param payload 报文
     */
    private void send(String key, StreamPayload<?> payload) {
        String topic = properties.getExecutorToSchedulerTopic();

        Message<String> message = MessageBuilder
                .withPayload(StreamUtils.toJson(payload))
                // KafkaHeaders
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, key)
                // StreamHeader
                .setHeader(StreamHeader.ID, IdUtils.generateUUID())
                .setHeader(StreamHeader.DOMAIN, payload.getDomain())
                .setHeader(StreamHeader.TYPE, payload.getType())
                .setHeader(StreamHeader.OEM, null)
                .setHeader(StreamHeader.PRODUCER, producer.getName())
                .setHeader(StreamHeader.PRODUCER_TIME, System.currentTimeMillis())
                .setHeader(StreamHeader.PRODUCER_INSTANCE, producer.getInstance())
                .build();
        kafkaTemplate.send(message);
    }
}
