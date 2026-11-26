package com.qianjh.ryzen.ims.dto;

import com.qianjh.ryzen.ims.enums.ExecutorToSchedulerTaskResult;
import lombok.Builder;

/**
 * 任务结果
 *
 * @param executorCode 执行器标识
 * @param taskId       任务ID
 * @param errorMessage 错误信息
 * @author QianJH
 */
@Builder(toBuilder = true)
public record ExecutorTaskResponse(
        Long assignmentId,
        String executorCode,
        Long taskId,
        ExecutorToSchedulerTaskResult result,
        String errorMessage,
        String receiptCode
) {
}
