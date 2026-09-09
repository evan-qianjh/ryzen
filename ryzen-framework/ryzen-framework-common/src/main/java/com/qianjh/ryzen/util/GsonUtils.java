package com.qianjh.ryzen.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

public final class GsonUtils {

    public final static Gson GSON = new Gson();

    /**
     *
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        return GSON.toJson(object);
    }

    public static Map<String, String> toStringMap(String json) {
        if (StringUtils.isBlank(json)) {
            return Collections.emptyMap();
        }
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        return GSON.fromJson(json, type);
    }
}
