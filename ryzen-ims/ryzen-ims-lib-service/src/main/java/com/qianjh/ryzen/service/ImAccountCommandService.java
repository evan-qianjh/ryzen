package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImAccountCommand;

public interface ImAccountCommandService extends IService<ImAccountCommand> {
    ImAccountCommand create(ImAccount imAccount, String command);

    boolean callback(ImAccountCommand entity, Boolean success, String errorMessage);
}
