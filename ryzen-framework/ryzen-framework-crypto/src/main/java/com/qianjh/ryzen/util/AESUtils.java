package com.qianjh.ryzen.util;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;


/**
 * @author QianJH
 */
public class AESUtils {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String PROVIDER = "BC";
    private static final String AES = "AES";
    private static final int IV_LENGTH = 16;

    private static final String MODE_CBC = "AES/CBC";
    private static final String MODE_ECB = "AES/ECB";

    public static final String ALGORITHM_CBC_PKCS5 = MODE_CBC + "/PKCS5Padding";
    public static final String ALGORITHM_ECB_PKCS7 = MODE_ECB + "/PKCS7Padding";
    public static final String ALGORITHM_ECB_PKCS5 = MODE_ECB + "/PKCS5Padding";
    private static final String ALGORITHM_DEFAULT = ALGORITHM_CBC_PKCS5;

    public static final String DECODE_TYPE_BASE64 = "BASE64";
    public static final String DECODE_TYPE_HEX = "HEX";

    /**
     * 加密
     *
     * @param secret    密钥
     * @param plaintext 明文
     * @return 密文
     */
    public static String encrypt(String secret, String plaintext) {
        return encrypt(secret, plaintext, ALGORITHM_DEFAULT);
    }

    /**
     * 加密
     *
     * @param secret    密钥
     * @param plaintext 明文
     * @param algorithm 算法
     * @return 密文
     */
    public static String encrypt(String secret, String plaintext, String algorithm) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm, PROVIDER);
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), AES);

            // CBC
            if (algorithm.startsWith(MODE_CBC)) {
                // iv
                byte[] iv = new byte[IV_LENGTH];
                SecureRandom.getInstanceStrong().nextBytes(iv);
                IvParameterSpec ivps = new IvParameterSpec(iv);

                // init
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivps);

                // data
                byte[] data = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

                // join
                byte[] join = new byte[iv.length + data.length];
                System.arraycopy(iv, 0, join, 0, iv.length);
                System.arraycopy(data, 0, join, iv.length, data.length);

                // encode
                return Base64.getEncoder().encodeToString(join);
            }
            // ECB
            else if (algorithm.startsWith(MODE_ECB)) {
                // init
                cipher.init(Cipher.ENCRYPT_MODE, keySpec);

                // data
                byte[] data = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

                // encode
                return Base64.getEncoder().encodeToString(data);
            } else {
                throw new UnsupportedOperationException("Unsupported algorithm: " + algorithm);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解密
     *
     * @param secret     密钥
     * @param ciphertext 密文
     * @return 明文
     */
    public static String decrypt(String secret, String ciphertext) {
        return decrypt(secret, ciphertext, DECODE_TYPE_BASE64, ALGORITHM_DEFAULT);
    }

    /**
     * 解密
     *
     * @param secret     密钥
     * @param ciphertext 密文
     * @param decodeType 解码方式
     * @param algorithm  算法
     * @return 明文
     */
    public static String decrypt(String secret, String ciphertext, String decodeType, String algorithm) {
        try {
            byte[] ciphertextBytes;
            if (decodeType.equals(DECODE_TYPE_BASE64)) {
                ciphertextBytes = Base64.getDecoder().decode(ciphertext);
            } else if (decodeType.equals(DECODE_TYPE_HEX)) {
                ciphertextBytes = Hex.decodeHex(ciphertext);
            } else {
                throw new UnsupportedOperationException("Unsupported decodeType: " + decodeType);
            }
            //
            byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
            //
            byte[] plaintext = decrypt(secretBytes, ciphertextBytes, algorithm);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解密
     *
     * @param secret     密钥
     * @param ciphertext 密文
     * @param algorithm  算法
     * @return 明文
     */
    public static byte[] decrypt(byte[] secret, byte[] ciphertext, String algorithm) {
        try {
            //
            Cipher cipher = Cipher.getInstance(algorithm, PROVIDER);
            SecretKeySpec keySpec = new SecretKeySpec(secret, AES);

            // CBC
            byte[] input;
            if (algorithm.startsWith(MODE_CBC)) {
                // split join

                // iv
                byte[] iv = new byte[IV_LENGTH];
                System.arraycopy(ciphertext, 0, iv, 0, iv.length);
                IvParameterSpec ivps = new IvParameterSpec(iv);

                // data
                input = new byte[ciphertext.length - iv.length];
                System.arraycopy(ciphertext, IV_LENGTH, input, 0, input.length);

                // init
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivps);
            }
            // ECB
            else if (algorithm.startsWith(MODE_ECB)) {
                input = ciphertext;

                // init
                cipher.init(Cipher.DECRYPT_MODE, keySpec);
            } else {
                throw new UnsupportedOperationException("Unsupported algorithm: " + algorithm);
            }

            // doFinal
            return cipher.doFinal(input);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        String secret = "dev_yubmNOoGrNeRSeAfSMx4orpljtu6";

        // 加密
        String plaintext = "";
        String encrypt = encrypt(secret, plaintext);
        System.out.println("encrypt > " + encrypt);

        // 解密
//        String ciphertext = "abc=";
//        String decrypt = decrypt(secret, ciphertext);
//        System.out.println("decrypt > " + decrypt);
    }

}
