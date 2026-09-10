package com.qianjh.ryzen.netty;

import com.qianjh.ryzen.netty.event.*;
import com.qianjh.ryzen.netty.handler.HeaderHandler;
import com.qianjh.ryzen.service.LoginService;
import com.qianjh.ryzen.topic.TopicManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * TODO 周期校对订阅内容中的clientId，不存在的清理掉，防止OOM
 *
 * @author QianJH
 */
public final class ClientManager {

    public static final String ATTR_ACCOUNT_ID = "account-id";

    private static final ChannelGroup CLIENTS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static TopicManager topicManager;
    private static LoginService loginService;

    public static void init(TopicManager tm, LoginService ls) {
        topicManager = tm;
        loginService = ls;
    }

    /**
     * 获取在线用户数
     *
     * @return 数量
     */
    public static int getOnline() {
        return CLIENTS.size();
    }


    /**
     * 添加
     *
     * @param client 会话
     */
    public static void add(Channel client) {
        CLIENTS.add(client);

        // 发布事件
        ClientEventBus.publish(new ClientConnectedEvent(client.id()));
    }

    /**
     * 获取
     *
     * @param clientId 客户端ID
     * @return 会话
     */
    public static Channel get(ChannelId clientId) {
        return CLIENTS.find(clientId);
    }

    /**
     * 获取所有
     *
     * @return 所有
     */
    public static ChannelGroup getAll() {
        return CLIENTS;
    }


    /**
     * 主动关闭
     *
     * @param clientId 客户端
     */
    public static void close(ChannelId clientId) {
        Channel client = get(clientId);
        if (client != null) {
            close(client);
        }
    }

    /**
     *
     * 主动关闭
     *
     * @param client 客户端
     */
    public static void close(Channel client) {
        client.close();
    }

    /**
     * 关闭事件
     *
     * @param client 会话
     */
    public static void onClosed(Channel client) {
        Long tenantId = HeaderHandler.getTenantId(client);

        // 移除订阅
//        TopicManager topicManager = SpringUtils.getApplicationContext().getBean(TopicManager.class);
        topicManager.unsubscribe(tenantId, client.id());

        // 登出
//        LoginService loginService = SpringUtils.getApplicationContext().getBean(LoginService.class);
        loginService.logout(client);

        // 发布事件
        ClientEventBus.publish(new ClientDisconnectedEvent(client.id()));

        // 会自动移除，没必要手动移除
//        CLIENTS.remove(client);
        // 会自动断开
//        client.close();
    }

    /**
     * 登录
     *
     * @param clientId  客户端ID
     * @param accountId 账户
     */
    public static void login(ChannelId clientId, Long accountId) {
        //
        Assert.notNull(accountId, "accountId must not be null");
        setAttrVal(clientId, ATTR_ACCOUNT_ID, String.valueOf(accountId));

        // 发布事件
        ClientEventBus.publish(new ClientLoginEvent(clientId, accountId));
    }

    /**
     * 登出
     *
     * @param client 客户端
     */
    public static void logout(Channel client) {
        Long accountId = getAccountId(client);

        //
        setAttrVal(client, ATTR_ACCOUNT_ID, null);

        // 发布事件
        if (accountId != null) {
            ClientEventBus.publish(new ClientLogoutEvent(client.id(), accountId));
        }
    }

    /**
     * 是否登录
     *
     * @param clientId 客户端ID
     * @return 是否登录
     */
    public static boolean isLogin(ChannelId clientId) {
        Long accountId = getAccountId(clientId);
        return Objects.nonNull(accountId);
    }

    /**
     * 获取账户
     *
     * @param clientId 客户端ID
     * @return 账户ID
     */
    public static Long getAccountId(ChannelId clientId) {
        return getLongAttrVal(clientId, ATTR_ACCOUNT_ID);
    }

    public static Long getAccountId(Channel client) {
        return getLongAttrVal(client, ATTR_ACCOUNT_ID);
    }


    /**
     * 设置属性
     *
     * @param clientId 客户端ID
     * @param attrKey  属性key
     * @param attrVal  属性val
     */
    public static void setAttrVal(ChannelId clientId, String attrKey, String attrVal) {
        Channel client = get(clientId);
        setAttrVal(client, attrKey, attrVal);
    }

    public static void setAttrVal(Channel client, String attrKey, String attrVal) {
        if (Objects.isNull(client)) {
            return;
        }
        client.attr(AttributeKey.valueOf(attrKey)).set(attrVal);
    }


    /**
     * 获取属性值
     *
     * @param clientId 客户端ID
     * @param attrKey  属性key
     * @return 属性值
     */
    public static String getAttrVal(ChannelId clientId, String attrKey) {
        Channel client = get(clientId);
        return getAttrVal(client, attrKey);
    }

    public static String getAttrVal(Channel client, String attrKey) {
        if (Objects.isNull(client)) {
            return null;
        }
        return client.attr(AttributeKey.<String>valueOf(attrKey)).get();
    }

    /**
     * 获取Long属性值
     *
     * @param clientId 客户端ID
     * @param attrKey  属性key
     * @return 属性值
     */
    public static Long getLongAttrVal(ChannelId clientId, String attrKey) {
        Channel client = get(clientId);
        return getLongAttrVal(client, attrKey);
    }

    public static Long getLongAttrVal(Channel client, String attrKey) {
        String attrVal = getAttrVal(client, attrKey);
        if (StringUtils.isBlank(attrVal)) {
            return null;
        }
        return Long.parseLong(attrVal);
    }
}
