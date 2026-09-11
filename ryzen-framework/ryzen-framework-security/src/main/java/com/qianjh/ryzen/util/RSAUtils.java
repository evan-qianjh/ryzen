package com.qianjh.ryzen.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author QianJH
 */
public final class RSAUtils {

    public static final String ALGORITHM_SHA1WithRSA = "SHA1withRSA";
    public static final String ALGORITHM_SHA256WithRSA = "SHA256WithRSA";
    public static final String ALGORITHM_MD5WITHRSA = "MD5withRSA";

    /**
     * 生成密钥对
     *
     * @return 密钥对
     */
    public static KeyPair buildKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构建私钥
     *
     * @param privateKey 私钥字符串
     * @return 私钥实体
     */
    public static PrivateKey buildPrivateKey(String privateKey) {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构建私钥
     *
     * @param privateKey 私钥字符串
     * @return 私钥实体
     */
    public static RSAPrivateKey buildRsaPrivateKey(String privateKey) {
        return (RSAPrivateKey) buildPrivateKey(privateKey);
    }

    /**
     * 构建公钥
     *
     * @param publicKey 公钥
     * @return 实体
     */
    public static PublicKey buildPublicKey(String publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构建RSA公钥
     *
     * @param publicKey 公钥
     * @return 实体
     */
    public static RSAPublicKey buildRsaPublicKey(String publicKey) {
        return (RSAPublicKey) buildPublicKey(publicKey);
    }

    /**
     * 构建签名
     *
     * @param privateKey 私钥
     * @param algorithm  算法
     * @param content    内容
     * @return 签名
     */
    public static String signBuild(RSAPrivateKey privateKey, String algorithm, String content) {
        return signBuild(privateKey, algorithm, content, StandardCharsets.UTF_8);
    }

    /**
     * 签名
     *
     * @param privateKey 密钥
     * @param algorithm  算法
     * @param content    内容
     * @param charset    字符集
     * @return 签名
     */
    public static String signBuild(RSAPrivateKey privateKey, String algorithm, String content, Charset charset) {
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(privateKey);
            signature.update(content.getBytes(charset));
            return Base64.encodeBase64String(signature.sign());
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加密
     *
     * @param publicKey 公钥
     * @param plaintext 明文
     * @return 密文
     */
    public static String encrypt(RSAPublicKey publicKey, String plaintext) {
        try {
            // 创建Cipher对象，用公钥加密
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            // 加密明文
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());

            // 将加密后的字节数组转换为Base64字符串并返回
            return java.util.Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解密
     *
     * @param rsaPrivateKey 私钥
     * @param ciphertext    密文
     * @return 明文
     */
    public static String decrypt(RSAPrivateKey rsaPrivateKey, String ciphertext) {
        // 密文解成byte
        byte[] ciphertextBytes = java.util.Base64.getDecoder().decode(ciphertext);
        // 解密
        byte[] decrypt = decrypt(rsaPrivateKey, ciphertextBytes);
        // 明文转字符串
        return new String(decrypt);
    }

    /**
     * 解密
     *
     * @param rsaPrivateKey 私钥
     * @param ciphertext    密文
     * @return 明文
     */
    public static byte[] decrypt(RSAPrivateKey rsaPrivateKey, byte[] ciphertext) {
        try {
            // 创建Cipher对象，用私钥解密
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);

            // 解密密文
            return cipher.doFinal(ciphertext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 验证签名
     *
     * @param publicKey 公钥
     * @param algorithm 算法
     * @param content   内容
     * @param sign      签名
     * @return 是否通过
     */
    public static boolean signVerify(RSAPublicKey publicKey, String algorithm, String content, String sign) {
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(publicKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.decodeBase64(sign));
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 转换成字符串
     *
     * @param publicKey 公钥
     * @return 字符串
     */
    public static String toString(PublicKey publicKey) {
        return java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * 转换成字符串
     *
     * @param privateKey 私钥
     * @return 字符串
     */
    public static String toString(PrivateKey privateKey) {
        return java.util.Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }
}
