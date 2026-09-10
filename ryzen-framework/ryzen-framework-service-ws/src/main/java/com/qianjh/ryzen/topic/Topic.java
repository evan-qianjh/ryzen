package com.qianjh.ryzen.topic;

import com.qianjh.ryzen.netty.ClientManager;
import io.netty.channel.ChannelId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author QianJH 
 */
public interface Topic {
    default Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }

    /**
     * 注意：此容器是所有实现类共享的全局对象
     * Map<tenantId, Map<clientId, Set<topicKey>>>
     */
    Map<Long, Map<ChannelId, Set<String>>> SUBSCRIBERS = new ConcurrentHashMap<>();

    /**
     * 获取topic名称
     *
     * @return 名称
     */
    String getName();

    /**
     * 是否需要登录
     *
     * @return 是否
     */
    boolean isNeedLogin();

    /**
     * 订阅
     *
     * @param tenantId  租户
     * @param clientId  会话
     * @param topicMeta 信息
     */
    default void subscribe(Long tenantId, ChannelId clientId, TopicMeta topicMeta) {
        Map<ChannelId, Set<String>> subscribers = SUBSCRIBERS.computeIfAbsent(tenantId, key -> new ConcurrentHashMap<>());
        // 订阅
        Set<String> subscribes = subscribers.computeIfAbsent(clientId, key -> new HashSet<>());
        // add
        subscribes.add(topicMeta.getKey());
        log().debug("subscribes : subscribe ::: size={}", subscribes.size());

        //
        onSubscribe(tenantId, clientId, topicMeta);
    }

    /**
     * 订阅
     *
     * @param tenantId  租户
     * @param clientId  会话
     * @param topicMeta 信息
     */
    default void onSubscribe(Long tenantId, ChannelId clientId, TopicMeta topicMeta) {
    }

    /**
     * 取消订阅
     *
     * @param tenantId  租户
     * @param clientId  会话
     * @param topicMeta 信息
     */
    default void unsubscribe(Long tenantId, ChannelId clientId, TopicMeta topicMeta) {
        Map<ChannelId, Set<String>> subscribers = SUBSCRIBERS.get(tenantId);
        if (subscribers == null || subscribers.isEmpty()) {
            return;
        }
        Set<String> subscribes = subscribers.get(clientId);
        if (subscribes == null || subscribes.isEmpty()) {
            return;
        }
        subscribes.remove(topicMeta.getKey());

        log().debug("subscribes : unsubscribe ::: size={}", subscribes.size());
        //
        onUnsubscribe(tenantId, clientId, topicMeta);
    }

    /**
     * 取消订阅
     *
     * @param tenantId  租户
     * @param clientId  会话
     * @param topicMeta 信息
     */
    default void onUnsubscribe(Long tenantId, ChannelId clientId, TopicMeta topicMeta) {

    }

    /**
     * 取消订阅
     *
     * @param tenantId 租户
     * @param clientId 会话
     */
    default void unsubscribe(Long tenantId, ChannelId clientId) {
        Map<ChannelId, Set<String>> subscribers = SUBSCRIBERS.get(tenantId);
        if (subscribers == null || subscribers.isEmpty()) {
            return;
        }
        subscribers.remove(clientId);

        //
        onUnsubscribe(tenantId, clientId);
    }

    default void onUnsubscribe(Long tenantId, ChannelId clientId) {

    }

    /**
     * 获取订阅者
     *
     * @param tenantId  租户
     * @param topicMeta 订阅
     * @return 订阅者
     */
    default List<ChannelId> getSubscribers(Long tenantId, TopicMeta topicMeta) {
        Map<ChannelId, Set<String>> subscribers = SUBSCRIBERS.get(tenantId);
        if (subscribers == null || subscribers.isEmpty()) {
            return Collections.emptyList();
        }

        return subscribers.entrySet().stream()
                .filter(entry -> entry.getValue().contains(topicMeta.getKey()))
                .map(Map.Entry::getKey)
                .toList();
    }


    /**
     * 获取指定账户的订阅者
     *
     * @param tenantId   租户
     * @param accountIds 账户
     * @param topicMeta  订阅
     * @return 订阅者
     */
    default List<ChannelId> getSubscribers(Long tenantId, Set<Long> accountIds, TopicMeta topicMeta) {
        //
        if (accountIds == null || accountIds.isEmpty()) {
            return Collections.emptyList();
        }
        //
        return getSubscribers(tenantId, topicMeta)
                .stream().filter(e -> {
                    Long accountId = ClientManager.getLongAttrVal(e, ClientManager.ATTR_ACCOUNT_ID);
                    if (accountId == null) {
                        return false;
                    }
                    return accountIds.contains(accountId);
                }).toList()
                ;
    }

    /**
     * 获取指定账户的订阅者
     *
     * @param tenantId  租户ID
     * @param accountId 账户
     * @param topicMeta 订阅
     * @return 订阅者
     */
    default List<ChannelId> getSubscribers(Long tenantId, Long accountId, TopicMeta topicMeta) {
        if (accountId == null) {
            return Collections.emptyList();
        }
        return getSubscribers(tenantId, Set.of(accountId), topicMeta);
    }
}
