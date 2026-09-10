package com.qianjh.ryzen.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author QianJH
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PushMessage<T> {
    private String id;

    private String topic;
    /**
     * 用来做topic下二级类目区分,例如，SUBSCRIBE、CREATED、STATE_UPDATED等等
     */
    private String scene;
    /**
     * 数据
     */
    private T data;
}
