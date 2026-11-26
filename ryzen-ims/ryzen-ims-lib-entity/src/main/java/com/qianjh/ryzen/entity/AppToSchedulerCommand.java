package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.ims.enums.SchedulerCommandType;
import com.qianjh.ryzen.ims.enums.ToSchedulerCommandDomainType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@TableName(_Schema.TABLE_PREFIX + "app_to_scheduler_command")
public class AppToSchedulerCommand {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long oemId;
    private Long tenantId;

    private SchedulerCommandType commandType;
    private Long domainId;
    private ToSchedulerCommandDomainType domainType;

    private Boolean success;
    private String errorMessage;
}
