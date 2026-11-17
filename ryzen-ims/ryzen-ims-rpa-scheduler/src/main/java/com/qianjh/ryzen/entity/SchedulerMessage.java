package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.enums.ImType;
import com.qianjh.ryzen.enums.MessageDirection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@TableName(_Schema.TABLE_PREFIX + "scheduler_message")
public class SchedulerMessage {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private ImType imType;
    private String messageCode;
    private MessageDirection direction;

    private String accountCode;
    private String conversationCode;
    private String senderCode;
    private String senderAvatar;
    private LocalDateTime sentAt;
    private String content;

    private LocalDateTime createdTime;
}
