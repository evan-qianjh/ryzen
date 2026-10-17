package com.qianjh.ryzen.framework.common.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class DateUtils {

    public static final String DEFAULT_FORMATTER = "yyyy-MM-dd";
    public static final String CLEAR_FORMATTER = "yyyyMMdd";

    /* java.time.LocalDate  */

    public static LocalDate parse(String formatDate) {
        return parse(formatDate, DEFAULT_FORMATTER);
    }

    public static LocalDate parse(String formatDate, String formatter) {
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

    /* java.util.Date  */


    public static String format(Date date) {
        return format(date, DEFAULT_FORMATTER);
    }

    public static String format(Date date, String format) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static Date parseDate(String formatDate) {
        return parseDate(formatDate, DEFAULT_FORMATTER);
    }

    public static Date parseDate(String formatDate, String formatter) {
        if (StringUtils.isBlank(formatDate)) {
            return null;
        }
        try {
            return new SimpleDateFormat(formatter).parse(formatDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
