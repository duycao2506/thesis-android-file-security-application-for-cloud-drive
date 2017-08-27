package thesis.tg.com.s_cloud.utils;

import android.support.annotation.NonNull;
import android.util.Base64;

import java.io.IOException;
import java.io.OutputStream;

import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.security.AESCipher;
import thesis.tg.com.s_cloud.security.HeaderHandler;
import thesis.tg.com.s_cloud.security.SecuredMachine;

/**
 * Created by admin on 5/12/17.
 */

public class SConnectOutputstream extends OutputStream {
    BaseApplication ba;
    OutputStream outputStream;
    boolean isEncrypted = true;
    boolean checkEncrypt = false;
    MyCallBack prgresslistenner;
    long progress = 0;

    byte[] header;

    SecuredMachine sm;

    public SecuredMachine getSm() {
        return sm;
    }

    public void setSm(SecuredMachine sm) {
        this.sm = sm;
    }

    public SConnectOutputstream(BaseApplication ba, OutputStream outputStream) {
        this.ba = ba;
        this.outputStream = outputStream;
    }

    public void setPrgresslistenner(MyCallBack prgresslistenner) {
        this.prgresslistenner = prgresslistenner;
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
    }

    @Override
    public void write(@NonNull byte[] b, int off, int len) throws IOException {


        int index = off;
        if (!checkEncrypt) {
            HeaderHandler hh = new HeaderHandler();
            byte[] emailb = ba.getDriveUser().getEmail().getBytes();
            byte[] halfkey = hh.getHalfKeyFromAPacket(emailb, b);
            if (halfkey != null) {
                sm = AESCipher.initiateSecuredMachine(ba.getSimpleCipher(),
                        Base64.encodeToString(halfkey, Base64.NO_WRAP),
                        ba.getDriveUser().getMainKey());
                index = emailb.length + halfkey.length;
            }
            checkEncrypt = true;
        }


        byte[] tmp = new byte[len - (index - off)];

        if (sm == null){
            System.arraycopy(b,index,tmp,0,len);
//            for (int i = index; i < len; i++) {
////                this.write((int) b[i]);
//                tmp[i-index] = b[i];
//            }
        }
        else{
            for (int i = index; i < len ; i++){
                tmp[i-index] =sm.decrypt(b[i]);
//                        this.write((int) sm.decrypt(b[i]));
            }
        }

        this.outputStream.write(tmp, 0, len - index);

        progress += len;

        if (prgresslistenner != null)
            this.prgresslistenner.callback(EventConst.PROGRESS_UPDATE, 1,progress);

    }

    @Override
    public void close() throws IOException {
        super.close();
        this.outputStream.close();
    }
}
