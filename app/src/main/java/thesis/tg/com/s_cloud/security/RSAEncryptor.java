package thesis.tg.com.s_cloud.security;
import android.util.Base64;
import android.util.Log;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * <h1>RSA Encryptor</h1>
 *
 * The encryptor is used for encrypting raw files and constructed with
 * predefined modulus and exponent, which serve both self-generated keys
 * and randomly generated keys
 *
 * Created by giangle on 7/9/17.
 */
public class RSAEncryptor extends RSACipher{
    private PublicKey publicKey;

    /**
     * <h1>RSAEncrpytor Constructor</h1>
     * This constructor is used for self-generated private key by a public modulus and
     * a private exponent
     *
     * @param modulus The string of a big integer contains the public modulus of the key
     * @param public_exponent The string of a big integer contains the private exponent
     * @return void
     * @exception InvalidKeySpecException when invalid modulus and exponent is provided
     * */
    public RSAEncryptor(String modulus, String public_exponent) throws InvalidKeySpecException{
        /*Convert the string of private exponent to big integer*/
        Log.e("MOD", modulus);
        Log.e("EXP", public_exponent);
        BigInteger public_exp = new BigInteger(1, Base64.decode(public_exponent,Base64.NO_WRAP));
        /*Convert the string of modulus to big integer*/
        BigInteger public_mod = new BigInteger(1, Base64.decode(modulus,Base64.NO_WRAP));
        /*Construct private key spec object from these two big integers*/
        RSAPublicKeySpec publicKey= new RSAPublicKeySpec(public_mod,public_exp);
        /*Attempt to generate private key object by using RSA algorithm*/
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.publicKey = keyFactory.generatePublic(publicKey);
            this.initEnCipher(RSA_CIPHER,this.publicKey);
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
     * @param publicKey The private key object randomly generated
     * */
    RSAEncryptor(PublicKey publicKey) throws InvalidKeyException {
        if(publicKey == null){
            throw new InvalidKeyException();
        }
        this.publicKey = publicKey;
        try {
            this.initEnCipher(RSA_CIPHER,publicKey);
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
     * @param raw An array of raw bytes that is needed to be encrypted
     * @return byte[] An array of encrypted bytes by the provided private key with RSA
     * @exception InvalidKeyException when the init private key is invalid
     * @exception BadPaddingException when invalid RSA padding is provided
     * @exception IllegalBlockSizeException when the raw size is invalid
     * */
    public String encrypt(String raw) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        if (this.publicKey == null){
            throw new InvalidKeyException();
        }
        return Base64.encodeToString(this.cipher.doFinal(raw.getBytes()),Base64.NO_WRAP);
    }

}
