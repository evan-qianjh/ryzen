package com.qianjh.ryzen.browser;

/**
 * 浏览器会话工厂
 *
 * @author QianJH
 */
public interface BrowserSessionFactory {
    /**
     * 打开浏览器会话
     *
     * @return 浏览器会话
     */
    BrowserSession open();
}
