package com.qianjh.ryzen.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MessageUtils {

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final DateTimeFormatter MONTH_DAY_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("M月d日 HH:mm:ss", Locale.CHINA);

    /**
     * 将聊天消息日期转换为毫秒时间戳
     * <p>
     * 支持：
     * 昨天 15:17:09
     * 11月15日 12:01:07
     * 09:55:32
     */
    public static Long toTimestamp(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("消息时间不能为空");
        }

        text = text.trim();

        LocalDateTime dateTime;

        //
        if (text.startsWith("昨天")) {
            LocalTime time = LocalTime.parse(
                    text.substring(2).trim(),
                    TIME_FORMATTER
            );
            dateTime = LocalDate.now(ZONE_ID)
                    .minusDays(1)
                    .atTime(time);

        }
        //
        else if (text.contains("月")) {
            dateTime = LocalDateTime.parse(
                    LocalDate.now(ZONE_ID).getYear() + "年" + text,
                    DateTimeFormatter.ofPattern("yyyy年M月d日 HH:mm:ss", Locale.CHINA)
            );
        }
        //
        else {
            LocalTime time = LocalTime.parse(text, TIME_FORMATTER);
            dateTime = LocalDate.now(ZONE_ID).atTime(time);
        }

        return dateTime.atZone(ZONE_ID).toInstant().toEpochMilli();
    }
}
