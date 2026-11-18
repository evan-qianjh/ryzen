
package com.qianjh.ryzen.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

public class MessageUtils {

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final DateTimeFormatter MONTH_DAY_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy年M月d日 HH:mm:ss", Locale.CHINA);

    /**
     * 将聊天消息日期转换为毫秒时间戳
     * <p>
     * 支持：
     * 昨天 15:17:09
     * 11月15日 12:01:07
     * 09:55:32
     * 星期一 15:17:09
     * 星期二 15:17:09
     */
    public static Long toTimestamp(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("消息时间不能为空");
        }

        text = text.trim();

        LocalDate today = LocalDate.now(ZONE_ID);
        LocalDateTime dateTime;

        if (text.startsWith("昨天")) {
            LocalTime time = LocalTime.parse(
                    text.substring(2).trim(), TIME_FORMATTER
            );
            dateTime = today.minusDays(1).atTime(time);

        } else if (text.startsWith("星期")) {
            String week = text.substring(0, 3);
            String timeText = text.substring(4).trim();

            DayOfWeek dayOfWeek = switch (week) {
                case "星期一" -> DayOfWeek.MONDAY;
                case "星期二" -> DayOfWeek.TUESDAY;
                case "星期三" -> DayOfWeek.WEDNESDAY;
                case "星期四" -> DayOfWeek.THURSDAY;
                case "星期五" -> DayOfWeek.FRIDAY;
                case "星期六" -> DayOfWeek.SATURDAY;
                case "星期日", "星期天" -> DayOfWeek.SUNDAY;
                default -> throw new IllegalArgumentException("不支持的星期格式: " + week);
            };

            LocalDate date = today.with(
                    TemporalAdjusters.previousOrSame(dayOfWeek)
            );

            LocalTime time = LocalTime.parse(timeText, TIME_FORMATTER);

            dateTime = date.atTime(time);
        } else if (text.contains("月")) {
            dateTime = LocalDateTime.parse(
                    today.getYear() + "年" + text,
                    MONTH_DAY_FORMATTER
            );

        } else {
            LocalTime time = LocalTime.parse(text, TIME_FORMATTER);
            dateTime = today.atTime(time);
        }

        return dateTime.atZone(ZONE_ID).toInstant().toEpochMilli();
    }
}
