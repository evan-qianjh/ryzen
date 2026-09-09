package com.qianjh.ryzen.exception;

/**
 * 无权限异常
 *
 * @author QianJH
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(Throwable throwable) {
        super(throwable);
    }
}
