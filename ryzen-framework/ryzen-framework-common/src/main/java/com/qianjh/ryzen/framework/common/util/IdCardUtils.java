package com.qianjh.ryzen.framework.common.util;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class IdCardUtils {

    private static final int CHINA_ID_CARD_LENGTH = 18;

    public static LocalDate getBirthday(String idCard) {
        if (StringUtils.isBlank(idCard) || idCard.length() != CHINA_ID_CARD_LENGTH) {
            return null;
        }
        String birth = idCard.substring(6, 14);
        return LocalDate.parse(birth, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}
