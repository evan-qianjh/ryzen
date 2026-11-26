package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.ims.enums.ToSchedulerTaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@TableName(_Schema.TABLE_PREFIX + "app_to_scheduler_task_response")
public class AppToSchedulerTaskResponse {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long oemId;
    private Long tenantId;

    private Long toSchedulerTaskId;

    private ToSchedulerTaskStatus status;
    private String message;
}
