package com.qianjh.ryzen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qianjh.ryzen.ims.enums.MessageContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * IM对话
 *
 * @author QianJH
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@TableName(_Schema.TABLE_PREFIX + "im_conversation")
public class ImConversation {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long oemId;
    private Long tenantId;

    // IM账户
    private Long imAccountId;

    // 对话唯一标识
    private String code;
    private String title;
    private String subTitle;
    private String avatar;

    // 当前接待账户
    private Long serviceAccountId;

    // 最新消息
    private Long lastMessageId;
    private String lastMessageContent;
    private MessageContentType lastMessageContentType;
    private LocalDateTime lastMessageTime;
}
