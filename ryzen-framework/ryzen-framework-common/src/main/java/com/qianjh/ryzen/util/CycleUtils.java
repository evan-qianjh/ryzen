package com.qianjh.ryzen.util;

import com.qianjh.ryzen.dict.CycleUnit;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

public final class CycleUtils {

    public static final String YEAR_FORMATTER = "yyyy";
    public static final String MONTH_FORMATTER = "yyyy-MM";
    public static final String WEEK_FORMATTER = "YYYY 'W'ww"; // 注意不是yyyy。2025-12-30是2026年的第一周
    public static final String DAY_FORMATTER = "yyyy-MM-dd";
    public static final String HOUR_FORMATTER = "yyyy-MM-dd HH";

    /**
     * 获取周期KEY
     *
     * @param cycleUnit 单位
     * @param dateTime  时间
     * @return KEY
     */
    public static String getCycleKey(CycleUnit cycleUnit, LocalDateTime dateTime) {
        Assert.notNull(cycleUnit, "cycleUnit must not be null");
        Assert.notNull(dateTime, "dateTime must not be null");

        return switch (cycleUnit) {
            case YEAR -> DateTimeUtils.format(dateTime, YEAR_FORMATTER);
            case MONTH -> DateTimeUtils.format(dateTime, MONTH_FORMATTER);
            case WEEK -> DateTimeUtils.format(dateTime, WEEK_FORMATTER);
            case DAY -> DateTimeUtils.format(dateTime, DAY_FORMATTER);
            case HOUR -> DateTimeUtils.format(dateTime, HOUR_FORMATTER);
        };
    }

    /**
     * 获取周期KEY
     *
     * @param cycleUnit 单位
     * @param date      日期
     * @return KEY
     */
    public static String getCycleKey(CycleUnit cycleUnit, LocalDate date) {
        Assert.notNull(cycleUnit, "cycleUnit must not be null");
        Assert.notNull(date, "date must not be null");

        return switch (cycleUnit) {
            case YEAR -> DateUtils.format(date, YEAR_FORMATTER);
            case MONTH -> DateUtils.format(date, MONTH_FORMATTER);
            case WEEK -> DateUtils.format(date, WEEK_FORMATTER);
            case DAY -> DateUtils.format(date, DAY_FORMATTER);
            case HOUR -> throw new UnsupportedOperationException();
        };
    }

    /**
     * 获取周期区间
     *
     * @param cycleUnit 单位
     * @param dateTime 时间
     * @return [)区间
     */
    public static Pair<LocalDateTime, LocalDateTime> getCycleBetween(CycleUnit cycleUnit, LocalDateTime dateTime) {
        LocalDateTime left;
        LocalDateTime right;

        switch (cycleUnit) {
            case YEAR -> {
                left = dateTime.withDayOfYear(1)
                        .toLocalDate()
                        .atStartOfDay();
                right = left.plusYears(1);
            }
            case MONTH -> {
                left = dateTime.withDayOfMonth(1)
                        .toLocalDate()
                        .atStartOfDay();
                right = left.plusMonths(1);
            }
            case WEEK -> {
                left = dateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                        .toLocalDate()
                        .atStartOfDay();
                right = left.plusWeeks(1);
            }
            case DAY -> {
                left = dateTime.toLocalDate()
                        .atStartOfDay();
                right = left.plusDays(1);
            }
            case HOUR -> {
                left = dateTime.withMinute(0).withSecond(0).withNano(0);
                right = left.plusHours(1);
            }
            default -> throw new UnsupportedOperationException();
        }
        return Pair.of(left, right);
    }

//    public static void main(String[] args) {
//        CycleUnit cycleUnit = CycleUnit.DAY;
//        int cycleOffset = 0;
//        LocalDate offsetDate = DateUtils.getOffsetDate(cycleUnit, cycleOffset);
//        String cycleKey = CycleUtils.getCycleKey(cycleUnit, offsetDate);
//        System.out.println(cycleKey);
//    }
}
