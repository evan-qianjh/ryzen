package com.qianjh.ryzen.header;

/**
 * 客户端附加请求头
 *
 * @author QianJH
 */
public final class ClientHeader {
    /**
     * 前缀
     */
    public static final String PREFIX = "ca-";
    /**
     * 客户端唯一标识
     * 32位UUID，应用初始化时生成，缓存本地
     */
    public static final String CLIENT_CODE = PREFIX + "client-code";
    /**
     * 客户端设备
     * iPhone 16
     */
    public static final String CLIENT_DEVICE = PREFIX + "client-device";
    /**
     * 客户端系统
     * ios,21
     */
    public static final String CLIENT_OS = PREFIX + "client-os";
    /**
     * 客户端应用
     * com.xxx.yyy,2.4.2
     */
    public static final String CLIENT_APP = PREFIX + "client-app";
    /**
     * 客户端宿主(可选)
     * 微信:wechat-mini-program
     * 支付宝:alipay-mini-program
     * 抖音:douyin-mini-program
     */
    public static final String CLIENT_HOST = PREFIX + "client-host";
}
