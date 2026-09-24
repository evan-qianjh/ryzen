package com.qianjh.ryzen.framework;


import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("ryzen.stream_outbox")
public class StreamOutbox {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * OEM
     */
    private Long oemId;
    /**
     * Topic
     */
    private String topic;
    /**
     * 分片Key
     */
    private String shardingKey;
    /**
     * order
     */
    private String domain;
    /**
     * order.created
     */
    private String type;
    /**
     * 报文体
     */
    private String payload;
    /**
     * 生产者
     */
    private String producer;
    /**
     * 生产者实例
     */
    private String producerInstance;

    /**
     * 锁定者
     */
    private String lockedBy;
    /**
     * 锁定时间
     */
    private LocalDateTime lockedTime;

    /**
     * 重试次数
     */
    private Integer retryCount;
    /**
     * 下一次重试时间
     */
    private LocalDateTime nextRetryTime;
}
