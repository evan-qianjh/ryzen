package com.qianjh.ryzen.stream.util;

import com.google.gson.reflect.TypeToken;
import com.qianjh.ryzen.stream.model.StreamPayload;
import com.qianjh.ryzen.util.GsonUtils;

import java.lang.reflect.Type;

/**
 * @author QianJH
 */
public final class StreamUtils {

    public static <T> T fromJson(String payload, Type typeOf) {
        return GsonUtils.GSON.fromJson(payload, typeOf);
    }

    public static <BODY> StreamPayload<BODY> fromJson(String payload, Class<BODY> body) {
        Type typeOf = TypeToken.getParameterized(StreamPayload.class, body).getType();
        return fromJson(payload, typeOf);
    }

    public static String toJson(Object object) {
        return GsonUtils.GSON.toJson(object);
    }

}
