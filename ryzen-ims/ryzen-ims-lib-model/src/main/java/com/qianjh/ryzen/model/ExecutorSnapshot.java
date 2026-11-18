package com.qianjh.ryzen.model;

import com.qianjh.ryzen.enums.ExecutorOperationType;
import com.qianjh.ryzen.enums.ExecutorStatus;
import com.qianjh.ryzen.enums.ImType;
import lombok.Builder;

/**
 * 执行器快照
 *
 * @param executorCode     执行器
 * @param accountCode      账号
 * @param status           状态
 * @param currentOperation 当前操作
 * @param currentTaskId    当前任务
 * @param errorMessage     错误消息
 * @author QianJH
 */
@Builder(toBuilder = true)
public record ExecutorSnapshot(
        ImType imType,
        String executorCode,
        String accountCode,
        Long assignmentId,
        Long fencingToken,
        ExecutorStatus status,
        ExecutorOperationType currentOperation,
        Long currentTaskId,
        String errorMessage,
        Long time
) {
}
