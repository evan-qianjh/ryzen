package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImConversationDispatchRecord;
import com.qianjh.ryzen.entity.ServiceAccount;
import com.qianjh.ryzen.ims.enums.ImConversationDispatchType;

public interface ImConversationDispatchRecordService extends IService<ImConversationDispatchRecord> {

    ImConversationDispatchRecord create(ImConversation conversation, ServiceAccount serviceAccount, ImConversationDispatchType type);
}
