package thesis.tg.com.s_cloud.security;

import android.util.Base64;

import java.security.NoSuchAlgorithmException;

import thesis.tg.com.s_cloud.entities.DriveUser;

/**
 * Created by CKLD on 7/19/17.
 */

public class HeaderHandler {
    public HeaderHandler() {

    }

    public byte[] getHeader(String email, String halfkey) throws NoSuchAlgorithmException {
        byte[] header;
        byte[] emailb = Base64.decode(email,Base64.NO_WRAP);
        byte[] halfkeyb = Base64.decode(halfkey,Base64.NO_WRAP);

        header = new byte[emailb.length + halfkeyb.length];
        for (int i = 0; i < halfkeyb.length; i++){
            halfkeyb[i] ^= emailb[ i % emailb.length];
        }
        header = AESCipher.concat(emailb,halfkeyb);
        return header;
    }


    public byte[] getHalfKeyFromAPacket(byte[] emailb,byte[] packet){
        boolean isEncoded = false;
        int packetidx;
        for (packetidx = 0;  packetidx < emailb.length; packetidx++){
            if (emailb[packetidx] != packet[packetidx]){
                return null;
            }
        }
        byte[] halfkey = new byte[8];
        for (int i = 0; i < 8; i++){
            halfkey[i] = (byte) (packet[packetidx] ^ emailb[i%emailb.length]);
            packetidx++;
        }
        return halfkey;
    }




}
