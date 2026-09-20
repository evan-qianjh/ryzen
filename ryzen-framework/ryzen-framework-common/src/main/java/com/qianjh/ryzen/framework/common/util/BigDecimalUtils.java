package com.qianjh.ryzen.framework.common.util;

import java.math.BigDecimal;

public class BigDecimalUtils {

    public static String toString(BigDecimal amount) {
        if (amount == null) {
            return null;
        }
        return amount.stripTrailingZeros().toPlainString();
    }

}
