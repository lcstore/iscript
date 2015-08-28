package com.lezo.iscript.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AESCodec {
    // 密钥算法
    public static final String KEY_ALGORITHM = "AES";

    // 加解密算法/工作模式/填充方式,Java6.0支持PKCS5Padding填充方式,BouncyCastle支持PKCS7Padding填充方式
    // public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    public static final String CIPHER_ALGORITHM = "AES/CTR/PKCS5Padding";

    /**
     * 生成密钥
     * 
     * @param keyWord
     */
    public static String initkey(int len, String keyWord) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM); // 实例化密钥生成器
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(keyWord.getBytes());

        kg.init(len, secureRandom); // 初始化密钥生成器:AES要求密钥长度为128,192,256位
        SecretKey secretKey = kg.generateKey(); // 生成密钥
        return Base64.encodeBase64String(secretKey.getEncoded()); // 获取二进制密钥编码形式
    }

    /**
     * 转换密钥
     */
    public static Key toKey(byte[] key) throws Exception {
        return new SecretKeySpec(key, KEY_ALGORITHM);
    }

    /**
     * 加密数据
     * 
     * @param data 待加密数据
     * @param key 密钥
     * @return 加密后的数据
     * */
    public static String encrypt(String data, String key) throws Exception {
        Key k = toKey(Base64.decodeBase64(key)); // 还原密钥
        // 使用PKCS7Padding填充方式,这里就得这么写了(即调用BouncyCastle组件实现)
        // Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM); // 实例化Cipher对象，它用于完成实际的加密操作
        cipher.init(Cipher.ENCRYPT_MODE, k); // 初始化Cipher对象，设置为加密模式
        return Base64.encodeBase64String(cipher.doFinal(data.getBytes())); // 执行加密操作。加密后的结果通常都会用Base64编码进行传输
    }

    /**
     * 解密数据
     * 
     * @param data 待解密数据
     * @param key 密钥
     * @return 解密后的数据
     * */
    public static String decrypt(String data, String key) throws Exception {
        Key k = toKey(Base64.decodeBase64(key));
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, k); // 初始化Cipher对象，设置为解密模式
        return new String(cipher.doFinal(Base64.decodeBase64(data))); // 执行解密操作
    }

    public static void main(String[] args) throws Exception {
        String password = "1TJMa1(MallTJ->Ma11TJ->1TJMa1)";
        password = "1TJMa1";
        String source = "站在云端，敲下键盘，望着通往世界另一头的那扇窗，只为做那读懂0和1的人。。";
        source = "1234567890";
        System.out.println("原文：" + source);

        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
        Key key = new SecretKeySpec(password.getBytes(), KEY_ALGORITHM);

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM); // 实例化Cipher对象，它用于完成实际的加密操作
        cipher.init(Cipher.ENCRYPT_MODE, key); // 初始化Cipher对象，设置为加密模式
        String encode = Base64.encodeBase64String(cipher.doFinal(source.getBytes()));
        System.err.println(encode);

        Cipher c = Cipher.getInstance("AES/CTR/NoPadding");
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(256);
        c.init(Cipher.ENCRYPT_MODE, kg.generateKey());

    }

    /**
     * 加密
     * 
     * @param content 需要加密的内容
     * @param password 加密密码
     * @return
     */
    public static byte[] newEncrypt(String content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     * 
     * @param content 待解密内容
     * @param password 解密密钥
     * @return
     */
    public static byte[] newDecrypt(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
