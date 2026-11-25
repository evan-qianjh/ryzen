package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.ims.enums.ServiceAccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接待账号
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@TableName(_Schema.TABLE_PREFIX + "service_account")
public class ServiceAccount {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long oemId;
    private Long tenantId;

    private Long accountId;
    private ServiceAccountStatus status;
    private Boolean online;
    private String remark;
}
