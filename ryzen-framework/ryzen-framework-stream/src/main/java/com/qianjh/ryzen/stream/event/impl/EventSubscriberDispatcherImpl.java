package com.qianjh.ryzen.stream.event.impl;

import com.qianjh.ryzen.stream.event.EventSubscriber;
import com.qianjh.ryzen.stream.event.EventSubscriberDispatcher;
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
public class EventSubscriberDispatcherImpl implements EventSubscriberDispatcher {

    private final Map<String, EventSubscriber<?>> SUBSCRIBERS = new HashMap<>();

    public EventSubscriberDispatcherImpl(List<EventSubscriber<?>> subscribers) {
        if (subscribers.isEmpty()) {
            log.info("没有EventSubscriber");
        }
        for (EventSubscriber<?> subscriber : subscribers) {
            String key = buildKey(subscriber.getDomain(), subscriber.getType());
            SUBSCRIBERS.put(key, subscriber);
            log.info("初始化EventSubscriber ::: {}", key);
        }
    }

    @Override
    public void dispatch(String domain, String type, String payload) {
        String key = buildKey(domain, type);

        EventSubscriber<?> subscriber = SUBSCRIBERS.get(key);

        if (subscriber == null) {
            log.debug("EventSubscriber 不存在 ::: {}", key);
            return;
        }

        subscriber.subscribeRaw(payload);
    }

    private String buildKey(String domain, String type) {
        return domain + ":" + type;
    }
}
