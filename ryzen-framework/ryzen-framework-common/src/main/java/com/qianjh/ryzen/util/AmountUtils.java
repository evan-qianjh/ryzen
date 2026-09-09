package com.qianjh.ryzen.util;

import com.qianjh.ryzen.util.dto.Amount;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class AmountUtils {
    private AmountUtils() {
    }

    public static Amount ofMajor(BigDecimal major, int scale) {
        if(major == null) {
            return null;
        }
        Long minor = toMinorUnit(major, scale);

        return Amount.builder()
                .major(major)
                .minor(minor)
                .scale(scale)
                .build();
    }

    public static Amount ofMinor(long minor, int scale) {
        BigDecimal major = toMajorUnit(minor, scale);
        return Amount.builder()
                .major(major)
                .minor(minor)
                .scale(scale)
                .build();
    }

    public static long toScale(long minor, int scale, int toScale) {
        if (scale == toScale) {
            return minor;
        }
        int x = toScale - scale;
        if (x > 0) {
            return new BigDecimal(minor).movePointRight(x).longValue();
        } else {
            return new BigDecimal(minor).movePointLeft(Math.abs(x)).longValue();
        }
    }

    /**
     * 货币单位->展示单位
     *
     * @param minor 货币
     * @param scale 精度
     * @return 元、美元等等
     */
    public static String toMajorUnitString(Long minor, int scale) {
        BigDecimal major = toMajorUnit(minor, scale);
        if (major == null) {
            return null;
        }
        return major.toPlainString();
    }

    /**
     * 货币单位->展示单位
     *
     * @param minor 货币
     * @param scale 精度
     * @return 元、美元等等
     */
    public static BigDecimal toMajorUnit(Long minor, int scale) {
        if (Objects.isNull(minor)) {
            return null;
        }

        return BigDecimal.valueOf(minor)
                .movePointLeft(scale)
                .setScale(scale, RoundingMode.UNNECESSARY);
    }

    /**
     * 展示单位->货币单位
     *
     * @param major 展示单位
     * @param scale 精度
     * @return 分、美分
     */
    public static Long toMinorUnit(String major, int scale) {
        if (StringUtils.isBlank(major)) {
            return null;
        }
        return toMinorUnit(new BigDecimal(major), scale);
    }

    /**
     * 展示单位->货币单位
     *
     * @param major 展示单位
     * @param scale 精度
     * @return 分、美分
     */
    public static Long toMinorUnit(BigDecimal major, int scale) {
        if (major == null) {
            return null;
        }
        return major
                .movePointRight(scale)
                .longValueExact();
    }
}
