package com.qianjh.ryzen.message;

import lombok.Builder;
import lombok.Getter;

/**
 * @author QianJH
 */
@Getter
@Builder
public class ResponseMessage<T> {
    /**
     * 与请求呼应
     */
    private String id;

    private Integer code;
    private String msg;

    private Action action;
    private String process;
    private T data;
}
