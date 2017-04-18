/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Class which deals with the encryption of data.
 *
 * @author David
 */
public class Encryptor {

    /**
     * The encryption key.
     */
    private static final String KEY = "jtillencryptkey1";

    /**
     * Method to encrypt an object. This method uses reflection to scan all
     * fields in an object and encrypt them. Only fields that are String are
     * encrypted by this method.
     *
     * @param o the object to encrypt.
     * @return the encrypted object.
     */
    public static Object encrypt(Object o) {
        Class cls = o.getClass();
        for (Field f : cls.getDeclaredFields()) {
            f.setAccessible(true);
            if (f.getType().toString().equals("class java.lang.String")) {
                try {
                    String s = (String) f.get(o);
                    s = encrypt(s);
                    f.set(o, s);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return o;
    }

    /**
     * Method to decrypt an object. This method uses reflection to scan all
     * fields in an object and decrypt them. Only fields that are Strings are
     * decrypted by this.
     *
     * @param o the object to decrypt.
     * @return the decrypted object.
     */
    public static Object decrypt(Object o) {
        Class cls = o.getClass();
        for (Field f : cls.getDeclaredFields()) {
            f.setAccessible(true);
            if (f.getType().toString().equals("class java.lang.String")) {
                try {
                    String s = (String) f.get(o);
                    s = decrypt(s);
                    f.set(o, s);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return o;
    }

    /**
     * Method to encrypt a String.
     *
     * @param text the String to encrypt.
     * @return the encrypted String.
     */
    public static String encrypt(String text) {
        try {
            byte[] encryptedBytes = encrypt(text.getBytes("UTF8"));

            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Method to decrypt a String.
     *
     * @param text the String to decrypt.
     * @return if decrypted String.
     */
    public static String decrypt(String text) {
        try {
            byte[] cipherText = Base64.getDecoder().decode(text);
            byte[] plainTextInBytes = decrypt(cipherText);
            return new String(plainTextInBytes, "UTF8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Method to encrypt a byte array.
     *
     * @param plain the byte array to encrypt.
     * @return the encrypted byte array.
     */
    public static byte[] encrypt(byte[] plain) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] raw = cipher.doFinal(plain);
            return raw;
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Method to decrypt a byte array.
     *
     * @param cipherText the byte array to decrypt.
     * @return the decrypted byte array.
     */
    public static byte[] decrypt(byte[] cipherText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            byte[] plainText = cipher.doFinal(cipherText);
            return plainText;
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
