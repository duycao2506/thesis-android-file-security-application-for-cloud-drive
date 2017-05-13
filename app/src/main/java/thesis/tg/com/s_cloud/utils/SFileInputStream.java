package thesis.tg.com.s_cloud.utils;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by admin on 5/12/17.
 */

public class SFileInputStream extends FileInputStream {
    public SFileInputStream(@NonNull String name) throws FileNotFoundException {
        super(name);
    }

    public SFileInputStream(@NonNull File file) throws FileNotFoundException {
        super(file);
    }

    public SFileInputStream(@NonNull FileDescriptor fdObj) {
        super(fdObj);
    }

    @Override
    public int read(@NonNull byte[] b, int off, int len) throws IOException {
        int a =  super.read(b, off, len);
        for (int i = off; i < off+len; i ++){
            b[i] = (byte) (b[i] ^ 2);
        }
        return a;
    }
}
