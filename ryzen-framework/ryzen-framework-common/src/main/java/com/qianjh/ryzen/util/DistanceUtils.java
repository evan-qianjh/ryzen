package com.qianjh.ryzen.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 */
public class DistanceUtils {
    private static final BigDecimal KM = new BigDecimal("1000");

    public static Double clean(Double distance) {
        return Math.ceil(distance);
    }

    /**
     * 格式化
     *
     * @param distance
     * @return
     */
    public static String format(Double distance) {
        BigDecimal val = new BigDecimal(String.valueOf(distance));
        // 1km以下
        if (val.compareTo(KM) < 0) {
            return new BigDecimal(String.valueOf(distance))
                    .setScale(0, RoundingMode.UP)
                    .stripTrailingZeros().toPlainString() + "m";
        }
        return val.divide(KM, 1, RoundingMode.HALF_UP).toPlainString() + "km";
    }
}
