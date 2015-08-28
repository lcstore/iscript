package com.lezo.iscript.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class ChrisVenessAES {

    // that should not be a singleton lazybones, it may contain state
    private static final CharsetEncoder ASCII_ENCODER = StandardCharsets.UTF_8.newEncoder()
            .onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);;

    private static SecretKey deriveKey(String password, int nBits) throws CharacterCodingException {
        try {
            ByteBuffer buf = ASCII_ENCODER.encode(CharBuffer.wrap(password));
            int nBytes = nBits / Byte.SIZE; // bits / Byte.SIZE;
            Cipher aesECB = Cipher.getInstance("AES/ECB/NoPadding");
            int n = aesECB.getBlockSize();
            byte[] pwBytes = new byte[nBytes];
            // so we only use those characters that fit in nBytes! oops!
            buf.get(pwBytes, 0, buf.remaining());
            SecretKey derivationKey = new SecretKeySpec(pwBytes, "AES");
            aesECB.init(Cipher.ENCRYPT_MODE, derivationKey);
            // and although the derivationKey is nBytes in size, we only encrypt 16 (the block size)
            byte[] partialKey = aesECB.doFinal(pwBytes, 0, n);
            byte[] key = new byte[nBytes];
            System.arraycopy(partialKey, 0, key, 0, n);
            // but now we have too few so we *copy* key bytes
            // so only the increased number of rounds is configured using nBits
            System.arraycopy(partialKey, 0, key, n, nBytes - n);
            SecretKey derivatedKey = new SecretKeySpec(key, "AES");
            return derivatedKey;
        } catch (InvalidKeyException | NoSuchAlgorithmException
                | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException e) {
            throw new IllegalStateException("Key derivation should always finish", e);
        }
    }

    private static String decrypt(SecretKey aesKey, String encodedCiphertext) {
        try {
            // that's no base 64, that's base 64 over the UTF-8 encoding of the code points
            byte[] ciphertext = jsBase64Decode(encodedCiphertext);
            Cipher aesCTR = Cipher.getInstance("AES/CTR/NoPadding");
            int n = aesCTR.getBlockSize();
            byte[] counter = new byte[n];
            int nonceSize = n / 2;
            System.arraycopy(ciphertext, 0, counter, 0, nonceSize);
            IvParameterSpec iv = new IvParameterSpec(counter);
            aesCTR.init(Cipher.DECRYPT_MODE, aesKey, iv);
            byte[] plaintext = aesCTR.doFinal(ciphertext, nonceSize, ciphertext.length - nonceSize);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (InvalidKeyException | NoSuchAlgorithmException
                | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException | InvalidAlgorithmParameterException e) {
            throw new IllegalStateException(e);
        }
    }

    private static byte[] jsBase64Decode(String encodedCiphertext) {
        byte[] utf8CT = Base64.decodeBase64(encodedCiphertext);
        String cts = new String(utf8CT, StandardCharsets.UTF_8);
        byte[] ciphertext = new byte[cts.length()];
        for (int i = 0; i < cts.length(); i++) {
            ciphertext[i] = (byte) (cts.charAt(i) & 0xFF);
        }
        return ciphertext;
    }

    public static void main(String[] args) throws Exception {
        // SecretKey key = deriveKey("owlstead", 192);
        // // ciphertext may vary in length depending on UTF-8 encoding
        // String pt = decrypt(key, "XAHCqcOuwrZDblXCuhstw5Vrw5TClcOkwpVTaMOWQ8K0woYzTcKvKy8HZUDDgQ==");
        // System.out.println(pt);
        SecretKey key = deriveKey("1TJMa1", 256);
        // ciphertext may vary in length depending on UTF-8 encoding
        String pt = decrypt(key, "ce0de27e55be8f1b6411");
        System.out.println(pt);
    }
}
