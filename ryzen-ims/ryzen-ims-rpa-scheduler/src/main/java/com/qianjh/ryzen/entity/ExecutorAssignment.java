package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.ims.enums.ExecutorAssignmentStatus;
import com.qianjh.ryzen.ims.enums.ImType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * IM账户
 *
 * @author QianJH
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@TableName(_Schema.TABLE_PREFIX + "executor_assignment")
public class ExecutorAssignment {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long executorId;

    private ImType imType;
    private String accountCode;

    private ExecutorAssignmentStatus status;

    private LocalDateTime createdTime;
}
