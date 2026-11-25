package com.qianjh.ryzen.ims.model;

import com.qianjh.ryzen.ims.enums.ExecutorTaskStatus;
import lombok.Builder;

/**
 * 任务结果
 *
 * @param executorCode 执行器标识
 * @param taskId       任务ID
 * @param status       状态
 * @param errorMessage 错误信息
 * @param finishedTime   完成时间
 * @author QianJH
 */
@Builder(toBuilder = true)
public record ExecutorTaskResponse(
        Long assignmentId,
        String executorCode,
        Long taskId,
        ExecutorTaskStatus status,
        String errorMessage,
        Long finishedTime,
        String receiptCode
) {
}
