package com.qianjh.ryzen.browser;

import com.qianjh.ryzen.model.Assignment;

/**
 * 浏览器会话工厂
 *
 * @author QianJH
 */
public interface BrowserSessionFactory {
    /**
     * 打开浏览器会话
     *
     * @param assignment 指派
     * @return 浏览器会话
     */
    BrowserSession open(Assignment assignment);
}
