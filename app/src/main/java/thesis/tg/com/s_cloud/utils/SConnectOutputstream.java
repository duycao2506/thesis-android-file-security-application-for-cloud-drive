package thesis.tg.com.s_cloud.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by admin on 5/12/17.
 */

public class SConnectOutputstream extends OutputStream {

    byte[] header;
    OutputStream outputStream;
    boolean isEncrypted = true;
    boolean checkEncrypt = false;

    public SConnectOutputstream(byte[] header, OutputStream outputStream) {
        this.header = header;
        this.outputStream = outputStream;
    }


    public void setHeader(byte[] header) {
        this.header = header;
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

        Log.d("OFFSET", String.valueOf(off));

        this.outputStream.write(tmp, 0, len - index);

//        super.write(b,off,len);

    }
}
