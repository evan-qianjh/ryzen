package com.qianjh.ryzen.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 系统配置异常
 *
 * @author QianJH
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SystemConfigException extends RuntimeException {
    private String mc;
    private List<Object> ma;

    public SystemConfigException(String mc) {
        this.mc = mc;
        this.ma = null;
    }
}
