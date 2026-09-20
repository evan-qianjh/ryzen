package com.qianjh.ryzen.framework.websocket.service;

import com.qianjh.ryzen.framework.websocket.message.Message;
import com.qianjh.ryzen.framework.websocket.topic.TopicMeta;

/**
 *
 * @author QianJH 
 */
public interface PushMessageService {

    <T> Message build(T data, TopicMeta topicMeta);

    <T> Message build(T data, TopicMeta topicMeta, String scene);
}
