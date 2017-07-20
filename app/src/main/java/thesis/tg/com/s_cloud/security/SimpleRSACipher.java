package thesis.tg.com.s_cloud.security;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;


/**
 * <h1>Simple RSA Cipher</h1>
 * This class is implemented for performing RSA encryption and decryption
 * following the original algorithm of modular exponentiation as mathematics principle
 * Created by giangle on 7/11/17.
 */
public class SimpleRSACipher extends RSAKeyGenerator {


    /**
     * <h1>Constructor</h1>
     * Construct the object as the RSAKeyGenerator
     * for generating the key by using one unique identifier and
     * two non-unique identifiers
     * */
    public SimpleRSACipher(String uniqueId, String firstNonUnique, String secondNonUnique)
            throws UnsupportedEncodingException {
        super(uniqueId,firstNonUnique,secondNonUnique);
    }
    /**
     * <h1>Encrypt Key</h1>
     * Encrypt a string of text by performing modular exponentiation with
     * a public modulus and public exponent to each character of the string
     * and return a huge string that contain the encrypted key
     * */
    public String encryptKey(String key){
        StringBuilder sb = new StringBuilder();
        char[] array = key.toCharArray();
        for(int i = 0; i < array.length;i++){
            String encrypted = this.encrypt(new BigInteger(String.valueOf((int)array[i])));
            if(i==array.length-1){
                sb.append(encrypted);
                break;
            }
            sb.append(encrypted+":");
        }
        return sb.toString();
    }

    /**
     * <h1>Decrypt Key</h1>
     * Decrypt a string of text by performing modular exponentiation with
     * a public modulus and public exponent to each big integer separated by ':'
     * and return the raw key
     * */
    public String decryptKey(String key){
        StringBuilder sb = new StringBuilder();
        String[] charList = key.split(":");
        for(String a : charList){
            sb.append((char) Integer.parseInt(decrypt(new BigInteger(a))));
        }
        return sb.toString();
    }
    /**
     * <h1> Decrypt OTP</h1>
     * Decrypt the encrypted OTP code by performing modular exponentiation
     * on the encrypted OTP code and return the 6-digit raw code
     * */

    public String decryptOTP(String OTP){
        return decrypt(new BigInteger(OTP));
    }

    private String decrypt(BigInteger encrypted){
        return encrypted.modPow(this.privateExponent,this.modulus).toString();
    }
    private String encrypt(BigInteger raw){
        return raw.modPow(this.publicExponent,this.modulus).toString();
    }

    public BigInteger getPrivateComp() {
        return privateExponent;
    }
}
