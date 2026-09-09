package com.qianjh.ryzen.util;

import com.qianjh.ryzen.api.Mc;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public final class McUtils {
    private McUtils() {
    }

    public static String i18n(Mc mc) {
        Locale locale = LocaleContextHolder.getLocale();
        return i18n(mc, locale.toLanguageTag());
    }

    public static String i18n(Mc mc, String lang) {
        if ("zh-CN".equalsIgnoreCase(lang)) {
            return mc.getZh_cn();
        }
        return mc.getEn();
    }
}
