package id.ads.keycloak.bsso;

import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Scanner;

public class EncryptDecryptAES {
    private static final String aesEncryptionAlgorithem = "AES";
    private static Scanner sc;
    /**
     * Method for Encrypt Plain String Data * @param plainText
     *
     * @return encryptedText
     */
    public static String encrypt(String plainText, String keys) {
        String encryptedText = "";
        try {
            byte[] key = Base64.decode(keys);
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec secretKey = new SecretKeySpec(key,
                    aesEncryptionAlgorithem);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherText = cipher.doFinal(plainText.getBytes());
            encryptedText = new String(Base64.encode(cipherText));
        } catch (Exception e) {
            System.err.println("Encrypt Exception : " + e.getMessage());
        }
        return encryptedText;
    }

    /**
     * Method For Get encryptedText and Decrypted provided String * @param encryptedText
     *
     * @return decryptedText
     */
    public static String decrypt(String data, String keys) {
        String decryptedValue = "";
        try {
            byte[] bts = Base64.decode(data);
            byte[] key = Base64.decode(keys);
            SecretKeySpec secretKey = new SecretKeySpec(key,
                    aesEncryptionAlgorithem);
            Cipher c = Cipher.getInstance(aesEncryptionAlgorithem);

        } catch (Exception e) {
            System.err.println("Encrypt Exception : " + e.getMessage());
        }
        return decryptedValue;
    }

}
