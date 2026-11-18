package com.qianjh.ryzen.enums;

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
     * 	在线，未分配账号
     */
    IDLE,
    /**
     * 正在初始化浏览器及账号环境
     */
    INITIALIZING,
    /**
     * 	等待登录
     */
    WAIT_LOGIN,
    /**
     * 账号已登录，可以采集和发送
     */
    READY,
    /**
     * 正在执行采集或发送操作
     */
    BUSY,
    /**
     * 发生异常
     */
    ERROR
}
