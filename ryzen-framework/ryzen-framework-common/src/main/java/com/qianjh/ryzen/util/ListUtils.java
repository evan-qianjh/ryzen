package com.qianjh.ryzen.util;


import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ListUtils {
    /**
     * String -> List<Long>
     *
     * @param string 字符串
     * @return List
     */
    public static List<Long> string2LongList(String string) {
        if (StringUtils.isBlank(string)) {
            return Collections.emptyList();
        }
        return Arrays.stream(string.split(",")).map(Long::parseLong).toList();
    }

    /**
     * String -> List<String>
     *
     * @param string 字符串
     * @return List
     */
    public static List<String> string2StringList(String string) {
        return string2StringList(string, ",");
    }

    /**
     * String -> List<String>
     *
     * @param string    字符串
     * @param delimiter 分隔符
     * @return List
     */
    public static List<String> string2StringList(String string, String delimiter) {
        if (StringUtils.isBlank(string)) {
            return Collections.emptyList();
        }
        return Arrays.asList(string.split(delimiter));
    }

    public static String stringList2String(List<String> list) {
        if (Objects.isNull(list)) {
            return null;
        }
        if (list.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String s : list) {
            builder.append(s).append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static String longList2String(List<Long> list) {
        if (Objects.isNull(list)) {
            return null;
        }
        if (list.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Long s : list) {
            builder.append(s).append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
