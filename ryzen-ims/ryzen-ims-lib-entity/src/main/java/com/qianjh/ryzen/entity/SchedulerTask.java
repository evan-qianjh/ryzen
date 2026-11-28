package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.ims.enums.MessageContentType;
import com.qianjh.ryzen.ims.enums.SchedulerTaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@TableName(_Schema.TABLE_PREFIX + "scheduler_task")
public class SchedulerTask {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long oemId;
    private Long tenantId;

    private Long executorAssignmentId;
    private Long conversationId;
    private String content;
    private MessageContentType contentType;

    private SchedulerTaskStatus status;
    private Long serviceAccountId;
    private String imReceiptCode;
    private String responseMessage;
}
