package com.qianjh.ryzen.ims.model;

import com.qianjh.ryzen.ims.enums.ImType;
import com.qianjh.ryzen.ims.enums.SchedulerCommandType;
import lombok.Builder;

/**
 * 调度器命令
 *
 * @param code 编号
 * @param type 类型
 */
@Builder(toBuilder = true)
public record SchedulerCommandRequest(
        String code,
        SchedulerCommandType type,

        // type.assign用到
        ImType imType,
        String accountCode,

        // type.release/start/stop用到
        Long assignmentId
) {
}
