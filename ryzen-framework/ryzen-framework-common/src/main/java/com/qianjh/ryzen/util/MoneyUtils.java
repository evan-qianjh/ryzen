package com.qianjh.ryzen.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class MoneyUtils {

    private static final BigDecimal RMB_YUAN_TO_FEN_X = new BigDecimal(100);

    public static String toString(BigDecimal amount) {
        if (Objects.isNull(amount)) {
            return null;
        }
        return amount.stripTrailingZeros().toPlainString();
    }

    /**
     * 人民币 元转分
     *
     * @param amount 元
     * @return 分
     */
    public static BigDecimal rmbYuanToFen(BigDecimal amount) {
        return amount.multiply(RMB_YUAN_TO_FEN_X);
    }

    public static BigDecimal rmbFenToYuan(BigDecimal amount) {
        return amount.divide(RMB_YUAN_TO_FEN_X, 2, RoundingMode.DOWN);
    }

}
