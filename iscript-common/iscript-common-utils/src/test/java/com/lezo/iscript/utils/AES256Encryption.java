package com.lezo.iscript.utils;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AES256Encryption {

    /**
     * 密钥算法 java6支持56位密钥，bouncycastle支持64位
     * */
    public static final String KEY_ALGORITHM = "AES";

    /**
     * 加密/解密算法/工作模式/填充方式
     * 
     * JAVA6 支持PKCS5PADDING填充方式 Bouncy castle支持PKCS7Padding填充方式
     * */
    // public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS7Padding";
    public static final String CIPHER_ALGORITHM = "AES/CTR/PKCS7Padding";

    /**
     * 
     * 生成密钥，java6只支持56位密钥，bouncycastle支持64位密钥
     * 
     * @return byte[] 二进制密钥
     * */
    public static byte[] initkey() throws Exception {

        // 实例化密钥生成器
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM, "BC");
        // 初始化密钥生成器，AES要求密钥长度为128位、192位、256位
        kg.init(256);
        // 生成密钥
        SecretKey secretKey = kg.generateKey();
        // 获取二进制密钥编码形式
        return secretKey.getEncoded();
        // 为了便于测试，这里我把key写死了，如果大家需要自动生成，可用上面注释掉的代码
        // return new byte[] { 0x08, 0x08, 0x04, 0x0b, 0x02, 0x0f, 0x0b, 0x0c,
        // 0x01, 0x03, 0x09, 0x07, 0x0c, 0x03, 0x07, 0x0a, 0x04, 0x0f,
        // 0x06, 0x0f, 0x0e, 0x09, 0x05, 0x01, 0x0a, 0x0a, 0x01, 0x09,
        // 0x06, 0x07, 0x09, 0x0d };
    }

    /**
     * 转换密钥
     * 
     * @param key 二进制密钥
     * @return Key 密钥
     * */
    public static Key toKey(byte[] key) throws Exception {
        // 实例化DES密钥
        // 生成密钥
        SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
        return secretKey;
    }

    /**
     * 加密数据
     * 
     * @param data 待加密数据
     * @param key 密钥
     * @return byte[] 加密后的数据
     * */
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        // 还原密钥
        Key k = toKey(key);
        /**
         * 实例化 使用 PKCS7PADDING 填充方式，按如下方式实现,就是调用bouncycastle组件实现 Cipher.getInstance(CIPHER_ALGORITHM,"BC")
         */
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
        // 初始化，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, k);
        // 执行操作
        return cipher.doFinal(data);
    }

    /**
     * 加密数据
     * 
     * @param data 待加密数据
     * @param key 密钥
     * @return byte[] 加密后的数据
     * */
    public static byte[] encrypt(byte[] data, Key k) throws Exception {
        /**
         * 实例化 使用 PKCS7PADDING 填充方式，按如下方式实现,就是调用bouncycastle组件实现 Cipher.getInstance(CIPHER_ALGORITHM,"BC")
         */
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
        // 初始化，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, k);
        // 执行操作
        return cipher.doFinal(data);
    }

    /**
     * 解密数据
     * 
     * @param data 待解密数据
     * @param key 密钥
     * @return byte[] 解密后的数据
     * */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // 欢迎密钥
        Key k = toKey(key);
        /**
         * 实例化 使用 PKCS7PADDING 填充方式，按如下方式实现,就是调用bouncycastle组件实现 Cipher.getInstance(CIPHER_ALGORITHM,"BC")
         */
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // 初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, k);
        // 执行操作
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, Key k) throws Exception {
        /**
         * 实例化 使用 PKCS7PADDING 填充方式，按如下方式实现,就是调用bouncycastle组件实现 Cipher.getInstance(CIPHER_ALGORITHM,"BC")
         */
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // 初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, k);
        // 执行操作
        return cipher.doFinal(data);
    }

    /**
     * @param args
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    public static void main(String[] args) throws UnsupportedEncodingException {

        String keyString = "1TJMa1";
        String stringToEncode = "1234567890";
        String destEncode = "ce0de27e55be8f1b6411";
        String str = "AES";
        str = stringToEncode;
        System.out.println("原文：" + str);

        // 初始化密钥
        Key key;
        try {
            key = getKey(keyString);
            System.out.print("密钥：");
            // for (int i = 0; i < key.length; i++) {
            // System.out.printf("%x", key[i]);
            // }
            System.out.print("\n");
            // 加密数据
            byte[] data = AES256Encryption.encrypt(str.getBytes(), key);
            System.out.print("加密后：");
            for (int i = 0; i < data.length; i++) {
                System.out.printf("%x", data[i]);
            }
            System.out.print("\n");

            // 解密数据
            data = AES256Encryption.decrypt(data, key);
            System.out.println("解密后：" + new String(data));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static SecretKeySpec getKey(String password) throws Exception {
        int saltLength = 8;

        SecureRandom random = new SecureRandom();

        // randomly generate salt
        byte[] salt = new byte[saltLength];
        random.nextBytes(salt);

        // generating key from passphrase and salt
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 1024, 256);
        SecretKey key = factory.generateSecret(spec);
        SecretKeySpec kspec = new SecretKeySpec(key.getEncoded(), "AES");
        return kspec;
        // // You can change it to 128 if you wish
        // int keyLength = 256;
        // byte[] keyBytes = new byte[keyLength / 8];
        // // explicitly fill with zeros
        // Arrays.fill(keyBytes, (byte) 0x0);
        //
        // // if password is shorter then key length, it will be zero-padded
        // // to key length
        // byte[] passwordBytes = password.getBytes("UTF-8");
        // int length = passwordBytes.length < keyBytes.length ? passwordBytes.length : keyBytes.length;
        // System.arraycopy(passwordBytes, 0, keyBytes, 0, length);
        // SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        // return key;
    }
}
