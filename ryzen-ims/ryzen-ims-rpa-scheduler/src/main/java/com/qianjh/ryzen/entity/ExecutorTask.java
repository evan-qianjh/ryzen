package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.ims.enums.ExecutorTaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@TableName(_Schema.TABLE_PREFIX + "executor_task")
public class ExecutorTask {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String commandCode;
    private Long assignmentId;
    private String conversationCode;
    private String content;

    private ExecutorTaskStatus status;

    private Long executorId;

    private LocalDateTime dispatchTime;
    private LocalDateTime acceptedTime;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private LocalDateTime deadlineTime;
    private String errorMessage;

    private String receiptCode;

    private LocalDateTime createdTime;
}
