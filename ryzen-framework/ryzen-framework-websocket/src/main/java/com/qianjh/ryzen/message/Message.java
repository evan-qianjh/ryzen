package com.qianjh.ryzen.message;

import com.qianjh.ryzen.util.IdUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author QianJH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Message {
    private String id;
    private String content;

    public static Message build(String content) {
        return Message.builder()
                .id(IdUtils.toString(System.nanoTime()))
                .content(content)
                .build();
    }

    public static Message build(String id, String content) {
        return Message.builder()
                .id(id)
                .content(content)
                .build();
    }
}
