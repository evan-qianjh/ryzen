package com.qianjh.ryzen.netty;

import com.qianjh.ryzen.message.Message;
import com.qianjh.ryzen.netty.handler.MessageHandler;
import com.qianjh.ryzen.topic.TopicMeta;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * @author QianJH
 */
@Slf4j
public class MessageSender {

    private static final Logger WEBSOCKET_SEND_LOGGER = LoggerFactory.getLogger("WEBSOCKET_SEND_LOGGER");


    public static void send(ChannelId clientId, Message message, TopicMeta topicMeta) {
        send(clientId, message, topicMeta.getKey());
    }

    /**
     * 发送消息
     *
     * @param clientId 客户端ID
     * @param message  消息
     * @param summary  摘要
     */
    public static void send(ChannelId clientId, Message message, String summary) {
        if (!MessageHandler.PONG.equalsIgnoreCase(summary)) {
            WEBSOCKET_SEND_LOGGER.debug("[↑][{}] ::: {} {}", clientId.asShortText(), message.getId(), summary);
        }

        Channel client = ClientManager.get(clientId);
        if (client == null) {
            return;
        }
        try {
            client.writeAndFlush(new TextWebSocketFrame(message.getContent()));
        } catch (Exception e) {
            ClientManager.close(client);
        }
    }

    /**
     * 发送
     *
     * @param subscribers 订阅者
     * @param message     消息
     * @param topicMeta   topic
     */
    public static void send(List<ChannelId> subscribers, Message message, TopicMeta topicMeta) {
        send(subscribers, message, topicMeta, 100);
    }

    /**
     * 发送
     *
     * @param subscribers      订阅者
     * @param message          消息
     * @param topicMeta        topic
     * @param semaphorePermits 限流并行度
     */
    public static void send(List<ChannelId> subscribers, Message message, TopicMeta topicMeta, int semaphorePermits) {
        Semaphore semaphore = new Semaphore(semaphorePermits);
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (ChannelId subscriber : subscribers) {
                executor.submit(() -> {
                    try {
                        semaphore.acquire();
                        MessageSender.send(subscriber, message, topicMeta);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        log.error("发送失败 subscriber={}", subscriber, e);
                    } finally {
                        semaphore.release();
                    }
                });
            }
        }
    }

    /**
     * 全体发送
     *
     * @param message 消息
     * @param summary 摘要
     */
    public static void sendAll(Message message, String summary) {
        WEBSOCKET_SEND_LOGGER.debug("[↑] ::: {} {}", message.getId(), summary);
        ClientManager.getAll().writeAndFlush(new TextWebSocketFrame(message.getContent()));
    }
}
