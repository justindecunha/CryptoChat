package com.example.jesus.cryptochat;

import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/**
 * This class handles the cryptography. Uses very simple asymmetric cryptography, and users
 * must share the password in order to read each others messages.
 */
public class SimpleCrypt extends AppCompatActivity {

    /**
     *
     * @param plainText plain text to encrypt
     * @param myPassword the password to encrypt with
     *
     * @return an encrypted string
     */
    public static String encrypt(final String plainText, String myPassword) throws Exception {
        myPassword = padPassword(myPassword);//creates a string of length 16
        final Key key = new SecretKeySpec(myPassword.getBytes(), Constants.ALGORITHM);//generates key from password
        final Cipher c = Cipher.getInstance(Constants.ALGORITHM); //the algorithm to use - AES
        c.init(Cipher.ENCRYPT_MODE, key);
        final byte[] encValue = c.doFinal(plainText.getBytes());

        return Base64.encodeToString(encValue, 0);
    }

    /**
     *
     * @param encryptedValue the encrypted string to decrypt
     * @param myPassword the password used to encrypt
     *
     * @return a decrypted string
     */
    public static String decrypt(final String encryptedValue, String myPassword) throws Exception {
        myPassword = padPassword(myPassword); //get a good password size

        final Key key = new SecretKeySpec(myPassword.getBytes(), Constants.ALGORITHM); //generates a key from password
        final Cipher c = Cipher.getInstance(Constants.ALGORITHM); // the algorithm to use - AES
        c.init(Cipher.DECRYPT_MODE, key);
        final byte[] decodedValue = Base64.decode(encryptedValue, 0); //Base64 to ensure each letter maps to an ascii
        final byte[] decValue = c.doFinal(decodedValue);

        return new String(decValue);
    }

    /**
     * Pads the password with 0s to get a 16 character string required for the crypto
     * @param password a password of any length
     *
     * @return string of length 16
     */
    private static String padPassword(String password) {
        if(password.length() < Constants.PWD_LENGTH) {
            final int padCount = Constants.PWD_LENGTH - password.length();
            StringBuilder passwordBuilder = new StringBuilder(password);
            for(int i = 0; i < padCount; i++) {
                passwordBuilder.append("0");
            }
            password = passwordBuilder.toString();
        } else if(password.length() > Constants.PWD_LENGTH) {
            password = password.substring(0, Constants.PWD_LENGTH);
        }
        
        return password;
    }
}
