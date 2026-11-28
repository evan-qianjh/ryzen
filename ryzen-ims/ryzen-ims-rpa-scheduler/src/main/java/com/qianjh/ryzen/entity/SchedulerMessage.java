package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.ims.enums.ImType;
import com.qianjh.ryzen.ims.enums.MessageContentType;
import com.qianjh.ryzen.ims.enums.MessageDirection;
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
    private String accountCode;
    private String messageCode;
    private MessageDirection direction;

    private String conversationCode;
    private String conversationTitle;
    private String conversationSubTitle;
    private String conversationAvatar;
    private String senderCode;
    private String senderName;
    private String senderAvatar;
    private LocalDateTime sentTime;
    private String content;
    private MessageContentType contentType;

    private LocalDateTime createdTime;
}
