package thesis.tg.com.s_cloud.security;

import java.util.ArrayList;

/**
 * Created by CKLD on 7/19/17.
 */

public class SecuredMachine {

    byte[] fullkey;
    int offset;
    int a = 0;


    boolean checked = false;
    public SecuredMachine(byte[] fullkey) {
        this.fullkey = fullkey;
        this.offset = 0;

    }

    //for decryptor
    public byte decrypt(byte b){
        byte db = (byte) (b ^ fullkey[offset % fullkey.length]);

        offset++;

        return db;
    }

    //for encryptor
    public byte encrypt(byte b){
        byte eb = (byte)(b ^ fullkey[offset % fullkey.length]);
        offset++;
        return eb;
    }

    public int getfullkeylength(){
        return fullkey.length;
    }
}
