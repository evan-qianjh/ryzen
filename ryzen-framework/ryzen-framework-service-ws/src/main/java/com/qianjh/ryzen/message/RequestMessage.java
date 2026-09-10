package com.qianjh.ryzen.message;

import com.google.gson.JsonObject;
import lombok.Data;

import java.util.List;

/**
 * @author QianJH
 */
@Data
public class RequestMessage {

    /**
     * 与响应呼应
     */
    private String id;

    /**
     * 动作类
     */
    private Action action;
    private List<String> params;

    /**
     * 操作类:order,order_cancel
     */
    private String process;
    private JsonObject body;
}
