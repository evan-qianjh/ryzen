package com.qianjh.ryzen.framework.websocket.message;

/**
 * 动作
 *
 * @author QianJH
 */
public enum Action {
    /**
     * 订阅
     */
    subscribe,
    /**
     * 取消订阅
     */
    unsubscribe,
    /**
     * 登录
     */
    login,
    /**
     * 登出
     */
    logout,
}
