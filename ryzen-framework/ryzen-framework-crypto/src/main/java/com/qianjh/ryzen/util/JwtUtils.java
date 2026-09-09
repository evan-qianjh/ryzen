package com.qianjh.ryzen.util;

import com.auth0.jwt.interfaces.Claim;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author QianJH
 */
public final class JwtUtils {

    public static Object getClaimValue(Claim claim, Class<?> type) {
        if (type == String.class) {
            return claim.asString();
        } else if (type == Integer.class || type == int.class) {
            return claim.asInt();
        } else if (type == Long.class || type == long.class) {
            return claim.asLong();
        } else if (type == Boolean.class || type == boolean.class) {
            return claim.asBoolean();
        } else if (type == Double.class || type == double.class) {
            return claim.asDouble();
        } else if (type == Date.class) {
            return claim.asDate();
        } else if (type == List.class) {
            return claim.asList(Object.class);
        } else if (type == Map.class) {
            return claim.asMap();
        }
        return null;
    }
}
