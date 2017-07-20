package thesis.tg.com.s_cloud.security;
import android.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * <h1>RSADecryptor</h1>
 * The decryptor object for decrypting encrypted string
 * using RSA algorithm
 *
 * Created by giangle on 7/9/17.
 */
public class RSADecryptor extends RSACipher{
    private PrivateKey privateKey;

    /**
     * <h1>RSADecryptor Constructor</h1>
     * The construction of RSA Decryptor with given modulus and exponent
     * @param modulus The string of modulus in the RSA public key
     * @param exponent The string of exponent in the RSA public key
     * @exception InvalidKeyException when invalid modulus and exponent is provided
     * */
    RSADecryptor(String modulus, String exponent) throws InvalidKeySpecException {
        /*Convert the string of public modulus to big integer*/
        BigInteger public_mod = new BigInteger(1, Base64.decode(modulus,Base64.NO_WRAP));
        /*Convert the string of public exponent to big integer*/
        BigInteger public_exp = new BigInteger(1,Base64.decode(exponent,Base64.NO_WRAP));

        RSAPrivateKeySpec privateKeySpec= new RSAPrivateKeySpec(public_mod,public_exp);
        /*Attempt to generate private key object by using RSA algorithm*/
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.privateKey= keyFactory.generatePrivate(privateKeySpec);
            this.initDeCipher(RSA_CIPHER,this.privateKey);
        } catch (NoSuchAlgorithmException e) {
            /*Exception for invalid algorithm*/
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }
    /**
     * <h1>RSAEncrpytor Constructor</h1>
     * This constructor is used for randomly generated key that is provided with
     * a PrivateKey object
     *
     * @param privateKey The private key object randomly generated
     * */
    RSADecryptor(PrivateKey privateKey) throws InvalidKeyException {
        if(privateKey == null){
            throw new InvalidKeyException();
        }
        this.privateKey = privateKey;
        try {
            this.initDeCipher(RSA_CIPHER,privateKey);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }
    /**
     * <h1>Encrypt Function</h1>
     * This constructor is used for randomly generated key that is provided with
     * a PrivateKey object
     *
     * @param encrypted An array of raw bytes that is needed to be encrypted
     * @return byte[] An array of encrypted bytes by the provided private key with RSA
     * @exception InvalidKeyException when the init private key is invalid
     * @exception BadPaddingException when invalid RSA padding is provided
     * @exception IllegalBlockSizeException when the raw size is invalid
     * */
    public String decryptToString(String encrypted) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
            UnsupportedEncodingException {
        return new String(this.decrypt(encrypted),"UTF-8");
    }

    public byte[] decrypt(String encrypted) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
            UnsupportedEncodingException {
        if (this.privateKey == null){
            throw new InvalidKeyException();
        }
        return this.cipher.doFinal(Base64.decode(encrypted.getBytes(), Base64.NO_WRAP));
    }
}
