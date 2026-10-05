package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@TableName(_Schema.TABLE_PREFIX + "apikey")
public class Apikey {
    @TableId
    private Long id;
    private Long oemId;
    private Long tenantId;
    private Long accountId;

    private String apikey;
    private String publicKey;
    private String memo;

    private Boolean enabled;
    private LocalDateTime createdTime;
}
