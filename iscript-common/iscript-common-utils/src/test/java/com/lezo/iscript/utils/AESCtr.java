package com.lezo.iscript.utils;

/*
 * Copyright 2011 Gerton ten Ham
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * AES Encryption/decryption in Counter mode.
 * 
 * The encryption/decryption method should be compatible with the javascript AES implementation found at:
 * http://www.movable-type.co.uk/scripts/aes.html
 * 
 * @author Gerton
 *
 */
public class AESCtr {
    private static final String ALGORITHM = "AES/CTR/NoPadding";
    private static final int BITS128 = 16;
    private static final int BITS256 = 32;

    /**
     * Strong (128 bits) encryption of message using first 16 characters from key.
     * 
     * @param key The encryption key (use 256bits hash!)
     * @param message The message to encrypt
     * @return base64 encoded string
     */
    public static String encrypt128(String key, String message) throws Exception {
        return Base64.encodeBase64String(encrypt(key, message, BITS128));
    }

    /**
     * Decryption of strong (128bits) encrypted string using first 16 characters from key.
     * 
     * @param key The encryption key (use 256bits hash!)
     * @param message The Base64 encoded message to decrypt
     * @return decrypted string (UTF-8 encoding)
     */
    public static String decrypt128(String key, String message) throws Exception {
        byte[] msg = Base64.decodeBase64(message);
        String decValue = new String(decrypt(key, msg, BITS128), "UTF-8");
        return decValue;
    }

    /**
     * Unlimited (256 bits) encryption of message using first 32 characters from key. <br/>
     * <br/>
     * Due to import-control restrictions imposed by some countries, the jurisdiction policy files shipped with the Java
     * 2 SDK, only permit strong cryptography to be used. An unlimited strength version of these files (that is, with no
     * restrictions on cryptographic strength) is available for download at
     * http://www.oracle.com/technetwork/java/javase/downloads/index.html.
     * 
     * @param key The encryption key (use 256bits hash!)
     * @param message The message to encrypt
     * @return base64 encoded string
     */
    public static String encrypt256(String key, String message) throws Exception {
        return Base64.encodeBase64String(encrypt(key, message, BITS256));
    }

    /**
     * Decryption of unlimited (256bits) encrypted string using first 32 characters from key. <br/>
     * <br/>
     * Due to import-control restrictions imposed by some countries, the jurisdiction policy files shipped with the Java
     * 2 SDK, only permit strong cryptography to be used. An unlimited strength version of these files (that is, with no
     * restrictions on cryptographic strength) is available for download at
     * http://www.oracle.com/technetwork/java/javase/downloads/index.html.
     * 
     * @param key The encryption key (use 256bits hash!)
     * @param message The Base64 encoded message to decrypt
     * @return decrypted string (UTF-8 encoding)
     */
    public static String decrypt256(String key, String message) throws Exception {
        byte[] msg = Base64.decodeBase64(message);
        String decValue = new String(decrypt(key, msg, BITS256), "UTF-8");
        return decValue;
    }

    /**
     * Private encryption method.
     * 
     * @param keystring
     * @param message
     * @param bits
     * @return bytearray containing encrypted message
     * @throws Exception
     */
    private static byte[] encrypt(String keystring, String message, int bits) throws Exception {
        byte[] encValue = null;
        SecureRandom random = new SecureRandom();
        byte[] nonceBytes = new byte[8];
        random.nextBytes(nonceBytes);
        IvParameterSpec nonce = new IvParameterSpec(Arrays.copyOf(nonceBytes, 16));

        Key key = generateKey(keystring, bits);
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key, nonce);
        byte[] ciphertextWithoutNonce = c.doFinal(message.getBytes("UTF-8"));
        encValue = Arrays.copyOf(nonceBytes, nonceBytes.length + ciphertextWithoutNonce.length);
        for (int i = 0; i < ciphertextWithoutNonce.length; i++) {
            encValue[i + 8] = ciphertextWithoutNonce[i];
        }

        return encValue;
    }

    /**
     * Private decryption method.
     * 
     * @param keystring
     * @param message
     * @param bits
     * @return bytearray containing decrypted message
     * @throws Exception
     */
    private static byte[] decrypt(String keystring, byte[] message, int bits) throws Exception {
        byte[] decValue = null;
        byte[] nonceBytes = Arrays.copyOf(Arrays.copyOf(message, 8), 16);
        IvParameterSpec nonce = new IvParameterSpec(nonceBytes);

        Key key = generateKey(keystring, bits);
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key, nonce);
        decValue = c.doFinal(message, 8, message.length - 8);

        return decValue;
    }

    /**
     * Generate encryption key based on provided keystring.
     * 
     * @param keystring
     * @param bits
     * @return Encryption key
     * @throws Exception
     */
    private static Key generateKey(String keystring, int bits) throws Exception {
        byte[] keyBytes = new byte[bits];
        byte[] key = new byte[bits];
        for (int i = 0; i < bits; i++) {
            keyBytes[i] = (byte) keystring.codePointAt(i);
        }
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        // Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        key = cipher.doFinal(keyBytes);
        // Expand key to original length of keybytes
        for (int i = 0; i < bits - 16; i++) {
            key[16 + i] = key[i];
        }

        return new SecretKeySpec(key, "AES");
    }

    public static void main(String[] args) throws Exception {
        String key = "1TJMa1";
        String message = "1234567890";
        System.err.println(encrypt256(key, message));
    }
}
