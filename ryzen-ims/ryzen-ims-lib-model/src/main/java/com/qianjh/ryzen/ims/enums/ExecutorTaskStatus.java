package com.qianjh.ryzen.ims.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExecutorTaskStatus {
    /**
     * scheduler收到app请求后创建默认
     */
    PENDING(false),
    /**
     * scheduler调度下发给executor
     */
    DISPATCHED(false),
    /**
     * executor反馈scheduler受理
     */
    ACCEPTED(false),
    /**
     * executor反馈scheduler开始执行
     */
    RUNNING(false),
    /**
     * executor反馈scheduler成功
     */
    SUCCESS(true),
    /**
     * executor反馈scheduler失败
     */
    FAILED(true),
    /**
     * executor反馈scheduler异常
     */
    UNKNOWN(true),
    /**
     * executor收到停止/重启时，反馈scheduler取消ACCEPTED任务
     * // TODO 是否应该将任务重新改为PENDING等待重新调度?
     */
    CANCELED(true),
    /**
     * 长时间处于PENDING状态
     */
    TIMEOUT(true),

    ;
    final boolean closed;
}
