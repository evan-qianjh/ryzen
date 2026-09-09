package com.qianjh.ryzen.util;

import com.qianjh.ryzen.api.RespMc;
import org.springframework.util.Assert;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Utils {

    private static final String MD5_ALGORITHM = "MD5";
    private static final int SALT_LENGTH = 6; // 盐值的字节长度

    /**
     * 使用 MD5 加密字符串
     *
     * @param input     待加密的字符串
     * @param substring 截取长度
     * @return 加密后的字符串
     */
    public static String encrypt(String input, int substring) {
        String encrypt = encrypt(input);

        Assert.isTrue(substring <= encrypt.length(), McUtils.i18n(RespMc.ILLEGAL_ARGUMENT));

        return encrypt.substring(0, substring);
    }

    /**
     * 使用 MD5 加密字符串
     *
     * @param input 待加密的字符串
     * @return 加密后的字符串
     */
    public static String encrypt(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        try {
            MessageDigest md = MessageDigest.getInstance(MD5_ALGORITHM);
            byte[] bytes = md.digest(input.getBytes()); // 加密
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    /**
     * 使用 MD5 和盐值加密字符串
     *
     * @param input 待加密的字符串
     * @param salt  盐值
     * @return 加密后的字符串
     */
    public static String encryptWithSalt(String input, String salt) {
        if (input == null || salt == null) {
            throw new IllegalArgumentException("Input and salt cannot be null");
        }
        return encrypt(encrypt(input) + salt);
    }

    /**
     * 验证密码是否匹配
     *
     * @param rawPassword 原始密码
     * @param salt        盐值
     * @param encrypted   加密后的密码
     * @return 是否匹配
     */
    public static boolean verify(String rawPassword, String salt, String encrypted) {
        if (rawPassword == null || salt == null || encrypted == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }

        String encryptedInput = encryptWithSalt(rawPassword, salt);
        return encryptedInput.equals(encrypted);
    }

}
