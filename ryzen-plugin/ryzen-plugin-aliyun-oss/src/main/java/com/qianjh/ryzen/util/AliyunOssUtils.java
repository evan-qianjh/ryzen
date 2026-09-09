package com.qianjh.ryzen.util;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class AliyunOssUtils {

    private static final Gson GSON = new Gson();
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static String getPolicy() {
        LocalDateTime dateTime = LocalDateTime.now().plusHours(1);
        String expiration = dateTime.format(TIME_FORMAT);

        Map<String, Object> map = new HashMap<>();
        map.put("expiration", expiration);

        // TODO conditions应该更严谨
        Object[] conditions = new Object[1];
        conditions[0] = new Object[]{"content-length-range", 0, 1048576000};
        map.put("conditions", conditions);

        String policy = GSON.toJson(map);
        byte[] policyBytes = policy.getBytes(StandardCharsets.UTF_8);
        String base64Policy = Base64.getEncoder().encodeToString(policyBytes);

        log.debug("getPolicy ::: base64Policy={}, policy={}", base64Policy, policy);
        return base64Policy;
    }

    public static String getSignature(String secret, String policy) {
        try {
            Mac hmacSha1 = Mac.getInstance("HmacSHA1");

            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA1");
            hmacSha1.init(secretKeySpec);

            byte[] hmacBytes = hmacSha1.doFinal(policy.getBytes());
            String signature = Base64.getEncoder().encodeToString(hmacBytes);

            log.debug("getSignature ::: signature={}, policy={}", signature, policy);
            return signature;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
