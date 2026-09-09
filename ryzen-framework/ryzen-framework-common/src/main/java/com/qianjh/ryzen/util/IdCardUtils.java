package com.qianjh.ryzen.util;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class IdCardUtils {

    private static final int CHINA_ID_CARD_LENGTH = 18;

    public static String hide(String idCard) {
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

    public static LocalDate getBirthday(String idCard) {
        if (StringUtils.isBlank(idCard) || idCard.length() != CHINA_ID_CARD_LENGTH) {
            return null;
        }
        String birth = idCard.substring(6, 14);
        return LocalDate.parse(birth, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}
