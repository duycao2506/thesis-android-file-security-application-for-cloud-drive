package thesis.tg.com.s_cloud.security;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

/**
 * <h1>RSAKeyGenerator</h1>
 * This class is implemented for generating RSA public key and private key
 * from a set of given string or number that is uniquely defined
 *
 * Created by giangle on 7/9/17.
 */
public class RSAKeyGenerator {
    private BigInteger firstPrime;
    private BigInteger secondPrime;
    private BigInteger totient;
    public final static BigInteger publicExponent = new BigInteger("65537");
    protected BigInteger privateExponent;
    public BigInteger modulus;

    /**
     * RSAKeyGenerator Constructor
     *
     * */
    public RSAKeyGenerator(String uniqueIdentifier, String firstNonUnique, String secondNonUnique)
            throws UnsupportedEncodingException {
        String[] key = refactor(uniqueIdentifier,firstNonUnique,secondNonUnique);
        StringBuilder[] sb = {new StringBuilder(), new StringBuilder()};
        for (int i =0;i<2;i++){
            for (char c : key[i].toCharArray())
                sb[i].append((int)c);
        }
        this.firstPrime = new BigInteger(sb[0].toString()).nextProbablePrime();
        this.secondPrime = new BigInteger(sb[1].toString()).nextProbablePrime();
        this.calculateKeyComponents();
    }
    private void calculateKeyComponents(){
        this.modulus = this.firstPrime.multiply(this.secondPrime);
        this.totient = this.firstPrime.subtract(BigInteger.ONE).multiply(this.secondPrime.subtract(BigInteger.ONE));
        this.privateExponent = RSAKeyGenerator.publicExponent.modInverse(this.totient);
    }
    private String[] refactor(String uniqueIdentifier, String firstNonUnique, String secondNonUnique){
        String[] result = {"",""};
        String regex = "[^a-zA-Z0-9]";
        result[0] += uniqueIdentifier.replaceAll(regex,"");
        result[0] += firstNonUnique.replaceAll(regex,"");
        result[1] += secondNonUnique.replaceAll(regex,"");
        return result;
    }

    public BigInteger getModulus(){
        return this.modulus;
    }

}
