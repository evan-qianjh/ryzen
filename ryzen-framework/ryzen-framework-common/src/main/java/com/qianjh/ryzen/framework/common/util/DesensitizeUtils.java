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

    /**
     * 地址脱敏。
     * <p>
     * 示例：
     * 浙江省杭州市西湖区文三路 123 号 2 幢 501 室
     * -> 浙江省杭州市西湖区文三路 ****
     * <p>
     * 北京市朝阳区建国路 88 号 SOHO 3Q A 座 1201
     * -> 北京市朝阳区建国路 ****
     * <p>
     * 地址为空时原样返回。
     *
     * @param address 原始地址
     * @return 脱敏后的地址
     */
    public static String maskAddress(String address) {
        if (address == null || address.isBlank()) {
            return address;
        }

        String value = address.trim();

        // 找到第一个详细门牌信息的起始位置。
        int index = findDetailStart(value);

        if (index >= 0) {
            String prefix = value.substring(0, index).trim();
            return prefix.isEmpty() ? MASK : prefix + " " + MASK;
        }

        // 没有识别到门牌信息时，保守地隐藏末尾部分。
        return maskAddressTail(value);
    }

    /**
     * 查找详细地址的起始位置。
     */
    private static int findDetailStart(String address) {
        String[] patterns = {
                "\\d+\\s*号",
                "\\d+\\s*幢",
                "\\d+\\s*栋",
                "\\d+\\s*单元",
                "\\d+\\s*室",
                "\\d+\\s*楼",
                "[A-Za-z]+\\s*座",
                "\\d+\\s*层"
        };

        int result = -1;

        for (String pattern : patterns) {
            java.util.regex.Matcher matcher =
                    java.util.regex.Pattern.compile(pattern)
                            .matcher(address);

            if (matcher.find()
                    && (result < 0 || matcher.start() < result)) {
                result = matcher.start();
            }
        }

        return result;
    }

    /**
     * 无法识别地址结构时，隐藏末尾约一半内容。
     */
    private static String maskAddressTail(String address) {
        int length = address.length();

        if (length <= 2) {
            return MASK;
        }

        int keep = Math.max(2, (int) Math.ceil(length * 0.6));
        keep = Math.min(keep, length - 1);

        return address.substring(0, keep) + MASK;
    }
}
