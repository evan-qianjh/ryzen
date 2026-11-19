package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.enums.ExecutorOperationType;
import com.qianjh.ryzen.enums.ExecutorStatus;
import com.qianjh.ryzen.enums.ImType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@TableName(_Schema.TABLE_PREFIX + "executor")
public class Executor {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;

//    private ImType imType;
//    private String accountCode;
    private Long currentAssigmentId;

    private ExecutorStatus status;
    private ExecutorOperationType currentOperation;
    private String currentTaskId;
    private LocalDateTime registeredTime;
    private LocalDateTime lastHeartbeatTime;
    private String errorMessage;

    private LocalDateTime createdTime;
}
