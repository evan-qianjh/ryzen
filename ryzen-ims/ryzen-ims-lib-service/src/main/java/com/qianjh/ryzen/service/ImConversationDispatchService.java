package com.qianjh.ryzen.service;

import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImConversationDispatched;
import com.qianjh.ryzen.entity.ServiceAccount;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.ims.enums.ImConversationDispatchType;

public interface ImConversationDispatchService {
    /**
     * 调度
     *
     * @param conversation 对话
     * @return 服务账户
     */
    ServiceAccount dispatch(ImConversation conversation);

    /**
     * 调度
     *
     * @param conversation   对话
     * @param serviceAccount 服务账户
     * @param dispatchType   调度方式
     * @return 记录
     */
    ImConversationDispatched dispatch(ImConversation conversation, ServiceAccount serviceAccount,
                                      ImConversationDispatchType dispatchType, String remark, Operator operator);
}
