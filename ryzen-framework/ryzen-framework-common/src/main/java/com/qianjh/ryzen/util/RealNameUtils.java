package com.qianjh.ryzen.util;

import org.apache.commons.lang3.StringUtils;

public final class RealNameUtils {

    public static String hide(String realName) {
        if (StringUtils.isBlank(realName)) {
            return null;
        }
        realName = realName.trim();
        int length = realName.length();
        if(length < 2 || length > 5) {
            return realName;
        }
        //
        if(length == 2) {
            return realName.charAt(0) + "*";
        }

        StringBuilder stringBuilder = new StringBuilder(realName.trim());
        for (int i = 0; i < length; i++) {
            //
            if (i != 0 && i != length - 1) {
                stringBuilder.setCharAt(i, '*');
            }
        }
        return stringBuilder.toString();
    }
}
