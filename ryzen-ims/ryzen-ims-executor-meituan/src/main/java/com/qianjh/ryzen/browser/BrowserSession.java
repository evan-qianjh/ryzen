package com.qianjh.ryzen.browser;


import com.qianjh.ryzen.model.ExecutorMessage;

import java.util.List;

/**
 * 浏览器会话
 * <p>
 * 所有方法都必须由创建该会话的ExecutorWorker线程来调用。
 * <p>
 * All methods must be called by the executor worker thread that created the session.
 *
 * @author QianJH
 */
public interface BrowserSession extends AutoCloseable {
    /**
     * 是否已登录
     *
     * @return 是否
     */
    boolean isLoggedIn();

    /**
     * 采集消息
     *
     * @return 消息集合
     */
    List<ExecutorMessage> collectMessages();

    /**
     * 发送消息
     *
     * @param conversationCode 对话ID
     * @param content        内容
     */
    String sendMessage(String conversationCode, String content);

    @Override
    void close();
}
