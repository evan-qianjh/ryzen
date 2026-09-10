package com.qianjh.ryzen.service;

import com.qianjh.ryzen.message.Message;
import com.qianjh.ryzen.topic.TopicMeta;

/**
 *
 * @author QianJH 
 */
public interface PushMessageService {

    <T> Message build(T data, TopicMeta topicMeta);

    <T> Message build(T data, TopicMeta topicMeta, String scene);
}
