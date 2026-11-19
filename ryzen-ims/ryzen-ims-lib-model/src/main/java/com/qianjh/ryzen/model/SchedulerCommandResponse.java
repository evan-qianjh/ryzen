package com.qianjh.ryzen.model;

import com.qianjh.ryzen.enums.ImType;
import com.qianjh.ryzen.enums.SchedulerCommandType;
import lombok.Builder;

/**
 * 调度器命令
 *
 * @param code 编号
 * @param type 类型
 */
@Builder(toBuilder = true)
public record SchedulerCommandResponse(
        String code,
        SchedulerCommandType type,

        ImType imType,
        String accountCode,

        Long assignmentId,

        Boolean success,
        String errorMessage
) {
}
