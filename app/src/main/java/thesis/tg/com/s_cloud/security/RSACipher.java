package thesis.tg.com.s_cloud.security;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * <h1>RSACipher</h1>
 * The Cipher class for cipher initialization
 * in order to perform Encryption and Decryption
 * Created by giangle on 7/9/17.
 */
public class RSACipher {
    protected final static String RSA_CIPHER = "RSA";
    protected Cipher cipher;
    /**
     * Initialize the cipher with given instance
     * @param cipher the instance for the cipher initialization
     * */
    protected void initCipher(String cipher) throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.cipher = Cipher.getInstance(cipher);
    }
    /**
     * Initialize the encryption cipher with given instance and key
     * @param cipher the instance for the cipher initialization
     * @param key the encryption key for initialization
     * */
    protected void initEnCipher(String cipher, Key key) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException {
        initCipher(cipher);
        this.cipher.init(Cipher.ENCRYPT_MODE,key);
    }
    /**
     * Initialize the decryption cipher with given instance and key
     * @param cipher the instance for the cipher initialization
     * @param key the decryption key for initialization
     * */
    protected void initDeCipher(String cipher, Key key) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException {
        initCipher(cipher);
        this.cipher.init(Cipher.DECRYPT_MODE,key);
    }
}
