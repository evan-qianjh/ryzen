package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.SchedulerCommand;

public interface SchedulerCommandService extends IService<SchedulerCommand> {
    /**
     * 创建
     *
     * @param imAccount
     * @param command
     * @return
     */
    SchedulerCommand create(ImAccount imAccount, String command);

    /**
     *
     * @param entity
     * @param success
     * @param errorMessage
     * @return
     */
    boolean response(SchedulerCommand entity, Boolean success, String errorMessage);
}
