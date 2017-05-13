package thesis.tg.com.s_cloud.utils;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by admin on 5/12/17.
 */

public class SFileOutputStream extends FileOutputStream {
    public SFileOutputStream(@NonNull String name) throws FileNotFoundException {
        super(name);
    }

    public SFileOutputStream(@NonNull String name, boolean append) throws FileNotFoundException {
        super(name, append);
    }

    public SFileOutputStream(@NonNull File file) throws FileNotFoundException {
        super(file);
    }

    public SFileOutputStream(@NonNull File file, boolean append) throws FileNotFoundException {
        super(file, append);
    }

    public SFileOutputStream(@NonNull FileDescriptor fdObj) {
        super(fdObj);
    }

    @Override
    public void write(@NonNull byte[] b, int off, int len) throws IOException {
        for (int i = off; i < off+len; i ++){
            b[i] = (byte) (b[i] ^ 2);
        }
        super.write(b, off, len);

    }
}
