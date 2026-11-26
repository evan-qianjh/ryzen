package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImConversationDispatched;
import com.qianjh.ryzen.entity.ServiceAccount;
import com.qianjh.ryzen.ims.enums.ImConversationDispatchType;

public interface ImConversationDispatchedService extends IService<ImConversationDispatched> {

    ImConversationDispatched create(ImConversation conversation, ServiceAccount serviceAccount,
                                    ImConversationDispatchType type, String remark, Long beforeServiceAccountId);
}
