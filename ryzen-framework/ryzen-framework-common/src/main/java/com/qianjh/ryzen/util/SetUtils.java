package com.qianjh.ryzen.util;


import org.apache.commons.lang3.StringUtils;

import java.util.*;

public final class SetUtils {
    /**
     * @param string
     * @return
     */
    public static Set<String> string2Set(String string) {
        if (StringUtils.isBlank(string)) {
            return new HashSet<>();
        }
        return new HashSet<>(Arrays.asList(string.split(",")));
    }

    /**
     * @param strings
     * @return
     */
    public static Set<String> strings2Set(List<String> strings) {
        Set<String> result = new HashSet<>();
        for (String string : strings) {
            result.addAll(string2Set(string));
        }
        return result;
    }

    /**
     * @param set
     * @return
     */
    public static String set2String(Set<String> set) {
        if (Objects.isNull(set) || set.isEmpty()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (String s : set) {
            builder.append(s).append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
