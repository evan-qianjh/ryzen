package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.framework.common.entity._Schemas;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@TableName(_Schemas.TENANT + "account_token")
public class AccountToken {

    @TableId
    private Long id;
    private Long oemId;
    private Long tenantId;
    private Long accountId;

    private String refreshTokenId;
    private LocalDateTime generatedTime;
    private LocalDateTime expiredTime;
    private String clientInfo;
}
