package com.qianjh.ryzen.model;

import com.qianjh.ryzen.enums.ExecutorTaskStatus;
import com.qianjh.ryzen.enums.ImType;
import lombok.Builder;

/**
 * 任务结果
 *
 * @param executorCode 执行器标识
 * @param taskId       任务ID
 * @param status       状态
 * @param errorMessage 错误信息
 * @param finishedAt   完成时间
 * @author QianJH
 */
@Builder(toBuilder = true)
public record ExecutorTaskResponse(
        Long assignmentId,
        String executorCode,
//        ImType imType,
//        String accountCode,
        Long taskId,
        ExecutorTaskStatus status,
        String errorMessage,
        Long finishedAt,
        String receiptCode
) {
}
