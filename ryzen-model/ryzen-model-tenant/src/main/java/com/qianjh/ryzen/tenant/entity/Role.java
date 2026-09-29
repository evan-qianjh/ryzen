package com.qianjh.ryzen.tenant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 *
 * @author QianJH
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@TableName(_Database.SCHEMA + "role")
public class Role {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long oemId;
    private Long tenantId;

    private String title;

    private Boolean enabled;

    private LocalDateTime createdTime;
}
