package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImMessage;
import com.qianjh.ryzen.entity.ServiceAccount;
import com.qianjh.ryzen.ims.stream.scheduler_to_app.report.type.SchedulerToAppReportMessage;

public interface ImConversationService extends IService<ImConversation> {
    ImConversation getById(Long oemId, Long tenantId, Long id);

    ImConversation getByUk(Long oemId, Long tenantId, Long imAccountId, String code);

    ImConversation create(ImAccount imAccount, String code, String title, String subTitle, String avatar);

    ImConversation createIfAbsent(ImAccount imAccount, String code, String title, String subTitle, String avatar);

    boolean recordLastMessage(ImConversation entity, ImMessage message, SchedulerToAppReportMessage.Body schedulerMessage);

    boolean updateServiceAccount(ImConversation entity, ServiceAccount serviceAccount);
}
