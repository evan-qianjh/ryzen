package com.qianjh.ryzen.stream.model;

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
     * 时间
     */
    public static final String TIME = PREFIX + "time";
    /**
     * 租户ID
     */
    public static final String TENANT = PREFIX + "tenant";
    /**
     * 生产者
     */
    public static final String PRODUCER = PREFIX + "producer";
    /**
     * 生产者实例
     */
    public static final String PRODUCER_INSTANCE = PREFIX + "producer-instance";
}
