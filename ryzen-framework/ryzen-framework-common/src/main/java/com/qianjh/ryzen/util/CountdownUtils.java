package com.qianjh.ryzen.util;

import java.time.LocalDateTime;

public final class CountdownUtils {

    /**
     * 计算剩余秒数
     *
     * @param expireTime 过期时间
     * @return 剩余秒数
     */
    public static Long calcRemainingSecond(Long expireTime) {
        if (expireTime == null) {
            return null;
        }
        long diff = expireTime - System.currentTimeMillis();
        return diff > 0 ? diff / 1000 : 0L;
    }

    /**
     * 计算剩余秒数
     *
     * @param expireTime 过期时间
     * @return 剩余秒数
     */
    public static Long calcRemainingSecond(LocalDateTime expireTime) {
        return calcRemainingSecond(DateTimeUtils.getTime(expireTime));
    }
}
