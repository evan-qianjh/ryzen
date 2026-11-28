package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.SchedulerTask;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ServiceAccount;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.ims.enums.SchedulerTaskStatus;
import com.qianjh.ryzen.service.dto.AppToSchedulerTaskContent;

public interface SchedulerTaskService extends IService<SchedulerTask> {
    SchedulerTask getById(Long oemId, Long tenantId, Long id);

    SchedulerTask create(ServiceAccount serviceAccount, ImAccount imAccount, ImConversation imConversation, AppToSchedulerTaskContent content);

    boolean updateStatus(SchedulerTask entity, SchedulerTaskStatus before, SchedulerTaskStatus after,
                         String imReceiptCode, String errorMessage, Operator operator);
}
