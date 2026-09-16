package com.qianjh.ryzen.stream.core.impl;

import com.qianjh.ryzen.stream.core.StreamSubscriber;
import com.qianjh.ryzen.stream.core.StreamSubscriberDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author QianJH
 */
@Slf4j
@Component
public class StreamSubscriberDispatcherImpl implements StreamSubscriberDispatcher {

    private final Map<String, StreamSubscriber<?>> SUBSCRIBERS = new HashMap<>();

    public StreamSubscriberDispatcherImpl(List<StreamSubscriber<?>> subscribers) {
        if (subscribers.isEmpty()) {
            log.info("未发现 StreamSubscriber");
        }
        for (StreamSubscriber<?> subscriber : subscribers) {
            String key = buildKey(subscriber.getDomain(), subscriber.getType());
            SUBSCRIBERS.put(key, subscriber);
            log.info("StreamSubscriber 初始化 ::: {}", key);
        }
    }

    @Override
    public void dispatch(String domain, String type, String payload) {
        String key = buildKey(domain, type);

        StreamSubscriber<?> subscriber = SUBSCRIBERS.get(key);

        if (subscriber == null) {
            log.debug("StreamSubscriber 不存在 ::: {}", key);
            return;
        }

        subscriber.subscribeRaw(payload);
    }

    private String buildKey(String domain, String type) {
        return domain + ":" + type;
    }
}
