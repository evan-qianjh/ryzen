package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.AppToSchedulerCommand;
import com.qianjh.ryzen.ims.enums.SchedulerCommandType;
import com.qianjh.ryzen.ims.enums.ToSchedulerCommandDomainType;

public interface AppToSchedulerCommandService extends IService<AppToSchedulerCommand> {
    /**
     * 创建
     *
     * @param oemId       OEM
     * @param tenantId    租户
     * @param commandType 指令类型
     * @param domainType  领域类型
     * @param domainId    领域ID
     * @return 指令
     */
    AppToSchedulerCommand create(Long oemId, Long tenantId, SchedulerCommandType commandType, ToSchedulerCommandDomainType domainType, Long domainId);

    /**
     * 创建
     *
     * @param domain      领域
     * @param commandType 指令类型
     * @return 指令
     */
    AppToSchedulerCommand create(ImAccount domain, SchedulerCommandType commandType);

    /**
     *
     * @param entity
     * @param success
     * @param errorMessage
     * @return
     */
    boolean response(AppToSchedulerCommand entity, Boolean success, String errorMessage);
}
