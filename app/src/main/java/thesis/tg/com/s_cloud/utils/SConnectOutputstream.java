package thesis.tg.com.s_cloud.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;

/**
 * Created by admin on 5/12/17.
 */

public class SConnectOutputstream extends OutputStream {

    byte[] header;
    OutputStream outputStream;
    boolean isEncrypted = true;
    boolean checkEncrypt = false;
    MyCallBack prgresslistenner;
    long progress = 0;

    public SConnectOutputstream(byte[] header, OutputStream outputStream) {
        this.header = header;
        this.outputStream = outputStream;
    }

    public void setPrgresslistenner(MyCallBack prgresslistenner) {
        this.prgresslistenner = prgresslistenner;
    }

    @Override
    public void write(int b) throws IOException {

    }

    @Override
    public void write(@NonNull byte[] b, int off, int len) throws IOException {


        int index = off;
        if (!checkEncrypt) {
            for (; index < header.length && index < len; index++) {
                if (header[index] != b[index]) {
                    index = off;
                    isEncrypted = false;
                    break;
                }
            }
            checkEncrypt = true;
        }


        byte[] tmp = new byte[len - (index - off)];


        for (int i = index; i < len; i ++){
            tmp[i-index] = isEncrypted? (byte) (b[i] ^ 2) : b[i];
        }


        this.outputStream.write(tmp, 0, len - index);

        progress+= len;

        if (prgresslistenner != null)
            this.prgresslistenner.callback(EventConst.PROGRESS_UPDATE, 1,progress);

    }

    @Override
    public void close() throws IOException {
        super.close();
        this.outputStream.close();
    }
}
