package com.qianjh.ryzen.util;

import java.security.SecureRandom;

public final class DisplayIdUtils {
    private static final SecureRandom RANDOM = new SecureRandom();

    private DisplayIdUtils() {
        // prevent instantiation
    }

    /**
     * 随机码类型
     */
    public enum CodeType {
        /**
         * 仅数字（0-9）
         */
        DIGITS("0123456789"),

        /**
         * 数字 + 字母（0-9 + A-Z）
         */
        DIGITS_AND_LETTERS("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");

        private final char[] chars;

        CodeType(String chars) {
            this.chars = chars.toCharArray();
        }

        char[] getChars() {
            return chars;
        }
    }

    /**
     * 生成指定位数的随机码
     *
     * @param length 位数，必须 > 0
     * @param type   随机码类型
     * @return 随机码字符串
     */
    public static String generate(int length, CodeType type) {
        if (length <= 0) {
            throw new IllegalArgumentException("length must be positive");
        }
        if (type == null) {
            throw new IllegalArgumentException("code type must not be null");
        }

        char[] chars = type.getChars();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(chars[RANDOM.nextInt(chars.length)]);
        }
        return sb.toString();
    }
}
