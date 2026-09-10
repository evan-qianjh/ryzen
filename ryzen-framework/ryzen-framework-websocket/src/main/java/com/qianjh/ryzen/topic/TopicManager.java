package com.qianjh.ryzen.topic;

import io.netty.channel.ChannelId;

import java.util.List;

/**
 *
 * @author QianJH
 */
public interface TopicManager {

    void subscribe(Long tenantId, ChannelId clientId, String topicKey);

    default void subscribe(Long tenantId, ChannelId clientId, List<String> topicKeys) {
        for (String topicKey : topicKeys) {
            subscribe(tenantId, clientId, topicKey);
        }
    }

    void unsubscribe(Long tenantId, ChannelId clientId, String topicKey);

    default void unsubscribe(Long tenantId, ChannelId clientId, List<String> topicKeys) {
        for (String topicKey : topicKeys) {
            unsubscribe(tenantId, clientId, topicKey);
        }
    }

    void unsubscribe(Long tenantId, ChannelId clientId);

}
