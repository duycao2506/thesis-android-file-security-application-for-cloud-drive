package thesis.tg.com.s_cloud.utils;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;

/**
 * Created by admin on 5/12/17.
 */

public class SConnectInputStream extends InputStream {
    InputStream is;


    public SConnectInputStream(InputStream is) {
        this.is = is;
    }

    @Override
    public int read() throws IOException {
        return 0;
    }

    @Override
    public int read(@NonNull byte[] b) throws IOException {
        return super.read(b);
    }



    @Override
    public int read(@NonNull byte[] b, int off, int len) throws IOException {
        int n =  this.is.read(b, off, len);
        for (int i = off; i < off+len; i ++){
            b[i] = (byte) (b[i] ^ 2);
        }
        return n;
    }
}
