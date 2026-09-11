package com.qianjh.ryzen.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;

/**
 * 3DES
 * @author QianJH
 */
public final class TripleDESUtils {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String PROVIDER = "BC";
    private static final String _3DES = "DESede";
    private static final String MODE_ECB = _3DES + "/ECB";
    public static final String ALGORITHM_ECB_ZERO = MODE_ECB + "/ZeroBytePadding";

    public static final String DECODE_TYPE_BASE64 = "BASE64";
    public static final String DECODE_TYPE_HEX = "HEX";


    /**
     * 加密
     *
     * @param plaintext  明文
     * @param secret     密钥
     * @param encodeType 编码方式
     * @param algorithm  算法
     * @return 密文
     */
    public static String encrypt(String secret, String plaintext, String encodeType, String algorithm) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm, PROVIDER);
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), _3DES);

            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            //
            byte[] ciphertextBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // encode
            if (encodeType.equalsIgnoreCase(DECODE_TYPE_BASE64)) {
                return Base64.encodeBase64String(ciphertextBytes);
            }
            //
            else if (encodeType.equalsIgnoreCase(DECODE_TYPE_HEX)) {
                return Hex.encodeHexString(ciphertextBytes, false);
            }
            //
            else {
                throw new UnsupportedOperationException("Unsupported encodeType " + encodeType);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解密
     *
     * @param ciphertext 密文
     * @param secret     密钥
     * @param decodeType 解码方式
     * @param algorithm  算法
     * @return 明文
     */
    public static String decrypt(String secret, String ciphertext, String decodeType, String algorithm) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm, PROVIDER);
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), _3DES);

            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            byte[] ciphertextBytes;
            if (decodeType.equalsIgnoreCase(DECODE_TYPE_BASE64)) {
                ciphertextBytes = Base64.decodeBase64(ciphertext);
            }
            //
            else if (decodeType.equalsIgnoreCase(DECODE_TYPE_HEX)) {
                ciphertextBytes = Hex.decodeHex(ciphertext);
            }
            //
            else {
                throw new UnsupportedOperationException("Unsupported decodeType: " + decodeType);
            }
            byte[] decrypted = cipher.doFinal(ciphertextBytes);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public static String keyTo24(String secretKey) {
        int length = 24;
        byte[] copy = new byte[length];
        byte[] secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(secretKeyBytes, 0, copy, 0, Math.min(secretKeyBytes.length, length));
        return new String(copy, StandardCharsets.UTF_8);
    }
}
