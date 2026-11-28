package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.ims.enums.SchedulerToAppTaskResponseResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@TableName(_Schema.TABLE_PREFIX + "scheduler_task_response")
public class SchedulerTaskResponse {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long oemId;
    private Long tenantId;

    private Long taskId;

    private SchedulerToAppTaskResponseResult result;
    private String message;
}
