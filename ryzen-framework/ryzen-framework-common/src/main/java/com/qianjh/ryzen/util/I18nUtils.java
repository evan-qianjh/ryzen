package com.qianjh.ryzen.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

public final class I18nUtils {

    private static final Gson GSON = new Gson();

    /**
     * @param i18nJson
     * @return
     */
    public static Map<String, String> jsonStringToMap(String i18nJson) {
        if (StringUtils.isBlank(i18nJson)) {
            return Collections.emptyMap();
        }
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        return GSON.fromJson(i18nJson, type);
    }

    /**
     * @param i18nMap
     * @return
     */
    public static String mapToJsonString(Map<String, String> i18nMap) {
        if (i18nMap == null) {
            return null;
        }
        return GSON.toJson(i18nMap);
    }
}
