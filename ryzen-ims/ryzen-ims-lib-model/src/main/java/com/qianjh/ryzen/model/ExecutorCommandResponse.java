package com.qianjh.ryzen.model;

import com.qianjh.ryzen.enums.ExecutorCommandType;
import lombok.Builder;

/**
 * 指令结果
 *
 * @param assignmentId 分配
 * @author QianJH
 */
@Builder(toBuilder = true)
public record ExecutorCommandResponse(
        String code,
        ExecutorCommandType type,
        String executorCode,
        Long assignmentId
) {
}
