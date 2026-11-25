package com.qianjh.ryzen.ims.enums;

/**
 * 执行器状态
 *
 * @author QianJH
 */
public enum ExecutorStatus {
    /**
     * 离线
     */
    OFFLINE,
    /**
     * 空闲，未分配账号
     */
    IDLE,
    /**
     * 正在初始化浏览器及账号环境
     */
    STARTING,
    /**
     * 等待登录
     */
    WAIT_LOGIN,
    /**
     * 已启动（瞬时）
     */
    STARTED,
    /**
     * 已就绪，可以采集和发送
     */
    READY,
    /**
     * 繁忙，正在执行采集或发送操作
     */
    BUSY,
    /**
     * 已停止（瞬时）
     */
    STOPPED,
    /**
     * 发生异常
     */
    ERROR
}
