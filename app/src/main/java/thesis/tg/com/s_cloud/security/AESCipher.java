package thesis.tg.com.s_cloud.security;

import android.util.Base64;


import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAKey;
import java.util.Arrays;
import java.util.Scanner;



/**
 * <h1>AESCipher</h1>
 * The AESCipher is implemented for processing bulk data encryption
 * with AES algorithm and a randomly generated AES key that is
 * used for encryption and store in the server
 *
 * Created by giangle on 7/13/17.
 */
public class AESCipher {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    /**
     * Generate new main key for fresh encryption, this is account-based
     * key which make it unalterable among devices within an account
     * */
    public static String generateNewMainKey() throws NoSuchAlgorithmException {
        byte[] key = generateKey();
        return Base64.encodeToString(key,Base64.NO_WRAP);
    }
    /**
     * Generate two separate key: backup key and key
     * Backup key will be sent to user email and key will be used for
     * new authorized device registration.
     * */
    public static String[] generateBackupKey(String mainKey) throws Exception {

        byte[] key = generateKey();
        byte[] backupKey = xor(key,Base64.decode(mainKey,Base64.NO_WRAP));
        return new String[]{
            Base64.encodeToString(key,Base64.NO_WRAP),
            Base64.encodeToString(backupKey,Base64.NO_WRAP)
        };
    }
    private static byte[] xor(byte[] list1, byte[] list2) throws Exception {
        if (list1.length > list2.length) {
            byte[] tmp = list2;
            list2 = list1;
            list1 = tmp;
        }
        for (int i = 0; i < list1.length; i++) {
            list2[i] ^= list1[i];
        }
        return list2;
    }

    public static SecuredMachine initiateSecuredMachine(SimpleRSACipher cipher, String halfKeystr, String encryptedKey) {
        String key = cipher.encryptKey(encryptedKey);
        byte[] halfKey = Base64.decode(halfKeystr,Base64.NO_WRAP);
        byte[] finalKey = concat(halfKey,Base64.decode(key,Base64.NO_WRAP));
        return new SecuredMachine(finalKey);
    }


    public static byte[] encrypt(SimpleRSACipher cipher, byte[] halfKey, String encryptedKey, FileInputStream inputStream,
                                 int fileLength, FileOutputStream outputStream) throws CryptoException {
        String key = cipher.encryptKey(encryptedKey);
        byte[] finalKey = concat(halfKey,Base64.decode(key,Base64.NO_WRAP));
        return doCrypto(Cipher.ENCRYPT_MODE, finalKey , inputStream, fileLength, outputStream);
    }

    public static void decrypt(SimpleRSACipher cipher, byte[] halfKey, String encryptedKey, FileInputStream inputStream,
                               int fileLength,FileOutputStream outputStream) throws CryptoException {
        String key = cipher.decryptKey(encryptedKey);
        byte[] finalKey = concat(halfKey,Base64.decode(key,Base64.NO_WRAP));

        doCrypto(Cipher.DECRYPT_MODE, finalKey, inputStream, fileLength, outputStream);
    }
    private static byte[] generateKey() throws NoSuchAlgorithmException {
        SecureRandom rand = new SecureRandom();
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(rand);
        generator.init(128);
        return Arrays.copyOfRange(generator.generateKey().getEncoded(),0,8);
    }

    private static byte[] doCrypto(int cipherMode, byte[] key, FileInputStream inputStream, int fileLength,
                                 FileOutputStream outputStream) throws CryptoException {
        try {
            byte[] inputBytes= new byte[fileLength];
            inputStream.read(inputBytes);

            //Init cipher
            Key secretKey = new SecretKeySpec(key, ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);

            //Do cipher
            byte[] outputBytes = cipher.doFinal(inputBytes);
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();
            return key;
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }
    public static byte[] concat(byte[] list1, byte[] list2){
        byte[] result = new byte[list1.length+list2.length];
        System.arraycopy(list1,0,result,0,list1.length);
        System.arraycopy(list2,0,result,list1.length,list2.length);
        return result;
    }
}
class CryptoException extends Exception {

    public CryptoException() {
    }

    public CryptoException(String message, Throwable throwable) {
        super(message, throwable);
    }
}