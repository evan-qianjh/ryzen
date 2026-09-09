package com.qianjh.ryzen.util;

public final class IdUtils {

    public static String toString(Long id) {
        return id == null ? null : id.toString();
    }

    public static Long ofString(String id) {
        if (!isLong(id)) {
            return null;
        }
        return Long.parseLong(id);
    }

    public static boolean isLong(String str) {
        if (str == null) {
            return false;
        }
        str = str.trim();
        if (str.isEmpty()) {
            return false;
        }
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
