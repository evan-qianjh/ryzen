package com.qianjh.ryzen.model;

import lombok.Builder;

/**
 * 指令结果
 *
 * @param assignmentId 分配
 * @author QianJH
 */
@Builder(toBuilder = true)
public record ExecutorCommandResponse(
        String executorCode,
        Long assignmentId
        // TODO
) {
}
