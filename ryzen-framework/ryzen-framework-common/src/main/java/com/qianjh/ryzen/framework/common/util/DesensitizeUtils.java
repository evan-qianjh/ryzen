package com.qianjh.ryzen.framework.common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 数据脱敏工具类。
 */
public final class DesensitizeUtils {

    private static final String MASK = "*";
    private static final int MOBILE_LENGTH = 11;
    private static final int CHINA_ID_CARD_LENGTH = 18;

    private DesensitizeUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 中国大陆手机号脱敏，保留前三位和后四位。
     * 例如：13812345678 -> 138****5678
     */
    public static String maskMobile(String mobile) {
        if (mobile == null || mobile.isEmpty()) {
            return mobile;
        }

        if (mobile.length() != MOBILE_LENGTH) {
            return mobile;
        }

        return mobile.substring(0, 3) + "****" + mobile.substring(7);
    }

    /**
     * 姓名脱敏，保留首尾字符，中间字符替换为 *。
     * 单字姓名统一替换为 *。
     * 例如：张三 -> 张*，张三丰 -> 张*丰。
     */
    public static String maskName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }

        int length = name.length();

        if (length == 1) {
            return MASK;
        }

        if (length == 2) {
            return name.charAt(0) + MASK;
        }

        return name.charAt(0)
                + MASK.repeat(length - 2)
                + name.substring(length - 1);
    }

    public static String maskIdCard(String idCard) {
        if (StringUtils.isBlank(idCard)) {
            return null;
        }
        int length = idCard.trim().length();

        if (idCard.length() != CHINA_ID_CARD_LENGTH) {
            return idCard;
        }

        StringBuilder stringBuilder = new StringBuilder(idCard.trim());
        for (int i = 0; i < length; i++) {
            //
            if (i >= 3 && i <= length - 1 - 4) {
                stringBuilder.setCharAt(i, '*');
            }
        }
        return stringBuilder.toString();
    }
}
