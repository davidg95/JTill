/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.*;

/**
 *
 * @author David
 */
public class Encryptor {

    public Encryptor() {
        start();
    }

    private void start() {
        try {
            KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
            SecretKey myDesKey = keygenerator.generateKey();

            Cipher desCipher;
            desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

            byte[] text = "No body can see me".getBytes();

            desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
            byte[] textEncrypted = desCipher.doFinal(text);
            System.out.println("Text- " + text);
            System.out.println("Encrypted- " + textEncrypted);

            desCipher.init(Cipher.DECRYPT_MODE, myDesKey);
            byte[] textDecrypted = desCipher.doFinal(textEncrypted);
            System.out.println("Decrypted- " + textDecrypted);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println("SOMETHING HAS GONE WRONG");
        }
    }
}
