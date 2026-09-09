package com.qianjh.ryzen.util;

import java.util.concurrent.ThreadLocalRandom;

public final class OtpUtils {
    /**
     * 生成随机数字验证码
     *
     * @param length           验证码位数（如 6 表示6位）
     * @param allowLeadingZero 是否允许以0开头
     * @return 生成的验证码字符串
     */
    public static String generate(int length, boolean allowLeadingZero) {
        if (length <= 0) {
            throw new IllegalArgumentException("验证码位数必须大于0");
        }

        int min = allowLeadingZero ? 0 : (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - 1;

        int code = ThreadLocalRandom.current().nextInt(min, max + 1);
        return String.format("%0" + length + "d", code); // 保证位数，补0（即使允许前导0也安全）
    }

    /**
     * 生成
     */
    public static String generate() {
        return generate(6, true);
    }

    /**
     * 缓存缓存Key
     *
     * @param tenantId 租户ID
     * @param scene    场景
     * @param channel  渠道
     * @param address  地址
     * @return cache key
     */
    public static String buildCacheKey(Long tenantId, String scene, String channel, String address) {
        String dataKey = Md5Utils.encrypt(String.format("%s:%s:%s", scene, channel, address));
        return String.format("otp:%s:%s", tenantId, dataKey);
    }
}
