package com.qianjh.ryzen.util;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public final class DateUtils {

    public static final String DEFAULT_FORMATTER = "yyyy-MM-dd";

    public static LocalDate format(String formatDate) {
        return format(formatDate, DEFAULT_FORMATTER);
    }

    public static LocalDate format(String formatDate, String formatter) {
        if (StringUtils.isBlank(formatDate)) {
            return null;
        }
        return LocalDate.parse(
                formatDate,
                DateTimeFormatter.ofPattern(formatter)
        );
    }

    public static String format(LocalDate date) {
        return format(date, DEFAULT_FORMATTER);
    }

    public static String format(LocalDate date, String format) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }

    public static Integer calcAge(LocalDate birthday) {
        if (birthday == null) {
            return null;
        }
        return Period.between(birthday, LocalDate.now()).getYears();
    }

}
