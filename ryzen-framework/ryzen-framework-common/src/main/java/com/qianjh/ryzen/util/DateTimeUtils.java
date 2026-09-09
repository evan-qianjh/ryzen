package com.qianjh.ryzen.util;

import com.qianjh.ryzen.dict.CycleUnit;
import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 *
 */
public final class DateTimeUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String format(LocalDateTime datetime, String format) {
        if (datetime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return datetime.format(formatter);
    }

    /**
     * 获取偏移时间
     *
     * @param cycleUnit
     * @param cycleOffset
     * @return
     */
    public static LocalDateTime getOffsetDateTime(CycleUnit cycleUnit, Integer cycleOffset) {
        if (cycleOffset == null) {
            cycleOffset = 0;
        }
        LocalDateTime now = LocalDateTime.now();
        return switch (cycleUnit) {
            case HOUR -> now.plusHours(cycleOffset);
            case DAY -> now.plusDays(cycleOffset);
            case WEEK -> now.plusWeeks(cycleOffset);
            case MONTH -> now.plusMonths(cycleOffset);
            case YEAR -> now.plusYears(cycleOffset);
        };
    }

    public static LocalDateTime parseLocalDateTime(String time) {
        if (StringUtils.isBlank(time)) {
            return null;
        }
        return LocalDateTime.parse(time, FORMATTER);
    }

    public static String getFormat(LocalDateTime time) {
        if (time == null) {
            return null;
        }
        return time.format(FORMATTER);
    }

    public static LocalDateTime getYesterdayStartTime() {
        return LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIDNIGHT);
    }

    public static LocalDateTime getYesterdayEndTime() {
        return LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX);
    }

    public static LocalDateTime getTodayStartTime() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
    }

    public static LocalDateTime getTodayEndTime() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
    }

    /**
     * 时间差值在范围内
     *
     * @param a            时间A
     * @param b            时间B
     * @param milliseconds 毫秒范围
     * @return 是否
     */
    public static boolean betweenLte(LocalDateTime a, LocalDateTime b, long milliseconds) {
        Duration duration = Duration.between(a, b);
        return duration.toMillis() <= milliseconds;
    }

    /**
     * 时间差值在范围外
     *
     * @param a            时间A
     * @param b            时间B
     * @param milliseconds 毫秒范围
     * @return 是否
     */
    public static boolean betweenGt(LocalDateTime a, LocalDateTime b, long milliseconds) {
        Duration duration = Duration.between(a, b);
        return duration.toMillis() > milliseconds;
    }


    /**
     * 获取毫秒
     *
     * @param datetime 时间
     * @return 毫秒
     */
    public static Long getTime(LocalDateTime datetime) {
        if (datetime == null) {
            return null;
        }
        return datetime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取秒
     *
     * @param datetime
     * @return
     */
    public static Long getSecond(LocalDateTime datetime) {
        return getTime(datetime) / 1000;
    }

    /**
     * 毫秒转LocalDateTime
     *
     * @param millis 毫秒
     * @return LocalDateTime
     */
    public static LocalDateTime getLocalDateTime(Long millis) {
        if (millis == null) {
            return null;
        }
        // 将毫秒数转换为 Instant
        Instant instant = Instant.ofEpochMilli(millis);
        // 获取系统默认时区
        ZoneId zoneId = ZoneId.systemDefault();
        // 将 Instant 转换为 LocalDateTime
        return LocalDateTime.ofInstant(instant, zoneId);
    }

    /**
     * 秒转毫秒
     *
     * @param seconds 秒
     * @return 毫秒
     */
    public static long secondToMilli(long seconds) {
        return seconds * 1000;
    }

    /**
     * 秒转LocalDateTime
     *
     * @param seconds 秒
     * @return LocalDateTime
     */
    public static LocalDateTime secondToLocalDateTime(long seconds) {
        return getLocalDateTime(secondToMilli(seconds));
    }
}
