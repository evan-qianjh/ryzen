package com.qianjh.ryzen.service.impl;

import com.qianjh.ryzen.message.Message;
import com.qianjh.ryzen.message.PushMessage;
import com.qianjh.ryzen.service.PushMessageService;
import com.qianjh.ryzen.topic.TopicMeta;
import com.qianjh.ryzen.util.GsonUtils;
import com.qianjh.ryzen.util.IdUtils;
import org.springframework.stereotype.Service;

/**
 *
 * @author QianJH 
 */
@Service
public class PushMessageServiceImpl implements PushMessageService {

    @Override
    public <T> Message build(T data, TopicMeta topicMeta) {
        return build(data, topicMeta, null);
    }

    @Override
    public <T> Message build(T data, TopicMeta topicMeta, String scene) {
        if (data == null) {
            return null;
        }
        String id = IdUtils.toString(System.nanoTime());
        PushMessage<T> pushMessage = PushMessage.<T>builder()
                .id(id)
                .topic(topicMeta.getKey())
                .scene(scene)
                .data(data)
                .build();

        String content = GsonUtils.GSON.toJson(pushMessage);

        return Message.builder()
                .id(id)
                .content(content)
                .build();
    }
}
