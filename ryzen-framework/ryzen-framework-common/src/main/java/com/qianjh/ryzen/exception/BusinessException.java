package com.qianjh.ryzen.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 业务异常
 *
 * @author QianJH
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BusinessException extends RuntimeException {
    private String mc;
    private List<Object> ma;

    public BusinessException(String mc) {
        this.mc = mc;
        this.ma = null;
    }
}
