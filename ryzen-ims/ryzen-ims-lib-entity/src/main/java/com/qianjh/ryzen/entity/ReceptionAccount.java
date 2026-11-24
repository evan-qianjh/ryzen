package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.enums.ReceptionAccountStatus;
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
@TableName(_Schema.TABLE_PREFIX + "reception_account")
public class ReceptionAccount {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long oemId;
    private Long tenantId;

    private Long accountId;
    private ReceptionAccountStatus status;
    private String remark;

}
