package com.qianjh.ryzen.util;

import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author QianJH
 */
public final class PasswordUtils {

    private static final String DIGITS = "0123456789";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{}|;:,.<>?";

    private static final SecureRandom RANDOM = new SecureRandom();


    /**
     * 哈希密码
     *
     * @param password 密码
     * @return hash后的密码
     */
    public static String hash(String password) {
        if (StringUtils.isBlank(password)) {
            return password;
        }
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * 验证密码
     *
     * @param plaintext 密码
     * @param hashed    hash后的密码
     * @return 匹配的
     */
    public static boolean check(String plaintext, String hashed) {
        return BCrypt.checkpw(plaintext, hashed);
    }

    /**
     * 生成
     *
     * @return 密码
     */
    public static String generate() {
        return generate(16, true, true, true, true);
    }

    /**
     * 生成
     *
     * @param length     长度
     * @param useDigits  使用数字
     * @param useLower   使用小写字母
     * @param useUpper   使用大写字母
     * @param useSpecial 使用特殊字符
     * @return 密码
     */
    public static String generate(int length,
                                  boolean useDigits,
                                  boolean useLower,
                                  boolean useUpper,
                                  boolean useSpecial) {

        if (length <= 0) {
            throw new IllegalArgumentException("length must be > 0");
        }

        List<String> enabledSets = new ArrayList<>();

        if (useDigits) enabledSets.add(DIGITS);
        if (useLower) enabledSets.add(LOWER);
        if (useUpper) enabledSets.add(UPPER);
        if (useSpecial) enabledSets.add(SPECIAL);

        if (enabledSets.isEmpty()) {
            throw new IllegalArgumentException("At least one character set must be enabled");
        }

        // 保证长度 >= 启用的字符集数量（确保每类至少一个）
        if (length < enabledSets.size()) {
            throw new IllegalArgumentException("length too short for selected character sets");
        }

        List<Character> result = new ArrayList<>();

        // 每种字符集至少放一个
        for (String set : enabledSets) {
            result.add(randomChar(set));
        }

        // 构建全集
        StringBuilder all = new StringBuilder();
        for (String set : enabledSets) {
            all.append(set);
        }

        // 填充剩余长度
        for (int i = result.size(); i < length; i++) {
            result.add(randomChar(all.toString()));
        }

        // 打乱顺序（避免前几位固定）
        Collections.shuffle(result, RANDOM);

        // 转字符串
        StringBuilder password = new StringBuilder();
        for (char c : result) {
            password.append(c);
        }

        return password.toString();
    }

    private static char randomChar(String str) {
        return str.charAt(RANDOM.nextInt(str.length()));
    }
}
