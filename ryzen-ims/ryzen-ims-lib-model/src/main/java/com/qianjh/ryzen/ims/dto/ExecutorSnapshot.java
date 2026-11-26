package com.qianjh.ryzen.ims.dto;

import com.qianjh.ryzen.ims.enums.ExecutorOperationType;
import com.qianjh.ryzen.ims.enums.ExecutorStatus;
import lombok.Builder;

/**
 * 执行器快照
 *
 * @param executorCode     执行器
 * @param assignmentId     分配
 * @param status           状态
 * @param currentOperation 当前操作
 * @param currentTaskId    当前任务
 * @param errorMessage     错误消息
 * @author QianJH
 */
@Builder(toBuilder = true)
public record ExecutorSnapshot(
        String executorCode,

        Long assignmentId,

        ExecutorStatus status,
        ExecutorOperationType currentOperation,
        Long currentTaskId,
        String errorMessage
) {
}
