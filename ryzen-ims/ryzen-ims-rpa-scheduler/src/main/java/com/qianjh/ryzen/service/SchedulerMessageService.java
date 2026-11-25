package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.SchedulerMessage;
import com.qianjh.ryzen.ims.enums.ImType;
import com.qianjh.ryzen.ims.model.ExecutorMessage;

public interface SchedulerMessageService extends IService<SchedulerMessage> {

    SchedulerMessage getByUk(ImType imType, String messageCode);

    SchedulerMessage create(ExecutorMessage message);

}
