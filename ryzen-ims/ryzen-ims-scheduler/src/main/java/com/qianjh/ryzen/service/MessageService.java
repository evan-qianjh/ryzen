package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.Message;
import com.qianjh.ryzen.enums.ImType;
import com.qianjh.ryzen.model.ExecutorMessage;

public interface MessageService extends IService<Message> {

    Message getByUk(ImType imType, String messageCode);

    Message create(ExecutorMessage message);

}
