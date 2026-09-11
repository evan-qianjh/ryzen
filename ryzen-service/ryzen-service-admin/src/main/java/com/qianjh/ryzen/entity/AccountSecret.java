package com.qianjh.ryzen.entity;

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
@TableName(_Schema.PREFIX + "account_secret")
public class AccountSecret {
    @TableId
    private Long id;
    private Long oemId;
    private Long tenantId;
    private Long accountId;
    /**
     * 登录密码 (加密)
     */
    private String loginPassword;
    /**
     * 基于时间的一次性密码(Time-based One-time Password) (加密)
     */
    private String totpSecret;
    private LocalDateTime createdTime;
}
