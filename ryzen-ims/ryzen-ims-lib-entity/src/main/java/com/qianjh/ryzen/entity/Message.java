package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName(_Schema.TABLE_PREFIX + "message")
public class Message {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long oemId;
    private Long tenantId;

    // 对话
    private Long conversationId;
    // 消息唯一标识
    private String messageCode;
    // 消息序列，用户分页排序
    private Long messageSeq;
    // 消息方向
    private MessageDirection direction;

//    private ImType imType;

    // 发送ID(仅MessageDirection.OUTBOUND有值)
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private LocalDateTime sentTime;
    private String content;

    private LocalDateTime createdTime;
}
