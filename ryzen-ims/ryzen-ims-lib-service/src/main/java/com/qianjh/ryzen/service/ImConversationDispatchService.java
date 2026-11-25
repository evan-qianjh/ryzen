package com.qianjh.ryzen.service;

import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ServiceAccount;

public interface ImConversationDispatchService {
    ServiceAccount dispatch(ImConversation conversation);
}
