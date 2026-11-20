package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.enums.ImAccountAssignStatus;
import com.qianjh.ryzen.enums.ImAccountExecutorStatus;
import com.qianjh.ryzen.enums.ImType;
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
@TableName(_Schema.TABLE_PREFIX + "im_account")
public class ImAccount {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long oemId;
    private Long tenantId;

    private ImType imType;
    private String code;
    private String remark;

    // NEW,ASSIGNING,ASSIGNED,RELEASING,RELEASED
    private ImAccountAssignStatus assignStatus;

    // NEW,STARTING,STARTED,STOPPING,STOPPED
    private ImAccountExecutorStatus executorStatus;

    private Long executorAssignmentId;

    private LocalDateTime createdTime;
}
