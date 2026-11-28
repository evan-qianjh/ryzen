package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.SchedulerCommand;
import com.qianjh.ryzen.ims.enums.SchedulerCommandType;
import com.qianjh.ryzen.ims.enums.SchedulerCommandDomainType;

public interface SchedulerCommandService extends IService<SchedulerCommand> {
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
    SchedulerCommand create(Long oemId, Long tenantId, SchedulerCommandType commandType, SchedulerCommandDomainType domainType, Long domainId);

    /**
     * 创建
     *
     * @param domain      领域
     * @param commandType 指令类型
     * @return 指令
     */
    SchedulerCommand create(ImAccount domain, SchedulerCommandType commandType);

    /**
     *
     * @param entity
     * @param success
     * @param errorMessage
     * @return
     */
    boolean response(SchedulerCommand entity, Boolean success, String errorMessage);
}
