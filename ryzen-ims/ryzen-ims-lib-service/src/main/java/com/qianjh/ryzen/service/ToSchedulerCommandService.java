package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ToSchedulerCommand;

public interface ToSchedulerCommandService extends IService<ToSchedulerCommand> {
    /**
     * 创建
     *
     * @param imAccount
     * @param command
     * @return
     */
    ToSchedulerCommand create(ImAccount imAccount, String command);

    /**
     *
     * @param entity
     * @param success
     * @param errorMessage
     * @return
     */
    boolean response(ToSchedulerCommand entity, Boolean success, String errorMessage);
}
