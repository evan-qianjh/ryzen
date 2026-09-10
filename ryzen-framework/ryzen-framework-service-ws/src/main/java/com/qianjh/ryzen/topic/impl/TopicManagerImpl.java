package com.qianjh.ryzen.topic.impl;

import com.qianjh.ryzen.exception.BusinessException;
import com.qianjh.ryzen.exception.UnauthorizedException;
import com.qianjh.ryzen.netty.ClientManager;
import com.qianjh.ryzen.topic.Topic;
import com.qianjh.ryzen.topic.TopicManager;
import com.qianjh.ryzen.topic.TopicMeta;
import io.netty.channel.ChannelId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author QianJH 
 */
@Slf4j
@Component
public class TopicManagerImpl implements TopicManager {
    private final Map<String, Topic> topics;

    public TopicManagerImpl(List<Topic> topics) {
        this.topics = topics.stream()
                .collect(Collectors.toUnmodifiableMap(
                        Topic::getName,
                        Function.identity()
                ));
    }

    private Topic getTopic(String name) {
        Topic topic = topics.get(name);
        if (topic == null) {
            throw new BusinessException("topic[" + name + "]不存在");
        }
        return topic;
    }

    @Override
    public void subscribe(Long tenantId, ChannelId clientId, String topicKey) {
        TopicMeta topicMeta = TopicMeta.ofKey(topicKey);
        Topic topic = getTopic(topicMeta.getName());
        if (topic.isNeedLogin() && !ClientManager.isLogin(clientId)) {
            throw new UnauthorizedException();
        }
        topic.subscribe(tenantId, clientId, topicMeta);
    }

    @Override
    public void unsubscribe(Long tenantId, ChannelId clientId, String topicKey) {
        TopicMeta topicMeta = TopicMeta.ofKey(topicKey);
        Topic topic = getTopic(topicMeta.getName());
        if (topic.isNeedLogin() && !ClientManager.isLogin(clientId)) {
            throw new UnauthorizedException();
        }
        topic.unsubscribe(tenantId, clientId, topicMeta);
    }

    @Override
    public void unsubscribe(Long tenantId, ChannelId clientId) {
        for (Topic topic : topics.values()) {
            topic.unsubscribe(tenantId, clientId);
        }
    }
}
