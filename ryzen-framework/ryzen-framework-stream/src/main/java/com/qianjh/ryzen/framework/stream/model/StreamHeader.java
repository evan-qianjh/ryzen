package com.qianjh.ryzen.framework.stream.model;

/**
 * @author QianJH
 */
public class StreamHeader {
    private static final String PREFIX = "stream-";
    /**
     * ID
     */
    public static final String ID = PREFIX + "id";
    /**
     * 领域
     */
    public static final String DOMAIN = PREFIX + "domain";
    /**
     * 类型
     */
    public static final String TYPE = PREFIX + "type";
    /**
     * OEM ID
     */
    public static final String OEM = PREFIX + "oem";
    /**
     * 生产者
     */
    public static final String PRODUCER = PREFIX + "producer";
    /**
     * 生产者时间
     */
    public static final String PRODUCER_TIME = PREFIX + "producer-time";
    /**
     * 生产者实例
     */
    public static final String PRODUCER_INSTANCE = PREFIX + "producer-instance";
}
