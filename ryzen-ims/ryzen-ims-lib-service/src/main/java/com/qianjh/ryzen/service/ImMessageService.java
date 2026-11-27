package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.AppToSchedulerTask;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImMessage;
import com.qianjh.ryzen.ims.stream.scheduler_to_app.report.SchedulerToAppReportMessage;

public interface ImMessageService extends IService<ImMessage> {

    ImMessage getById(Long oemId, Long tenantId, Long id);

    ImMessage getByUk(Long oemId, Long tenantId, Long conversationId, String code);

    ImMessage create(ImAccount imAccount, ImConversation conversation, SchedulerToAppReportMessage.Body message);

    ImMessage createIfAbsent(ImAccount imAccount, ImConversation conversation, SchedulerToAppReportMessage.Body message);

    ImMessage createByReceipt(AppToSchedulerTask task, String imReceiptCode);

    ImMessage createByReceiptIfAbsent(AppToSchedulerTask task, String imReceiptCode);

    void fillFromReceiptMissInfo(ImMessage entity, SchedulerToAppReportMessage.Body body);
}
