package thesis.tg.com.s_cloud.utils;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;

import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;

/**
 * Created by admin on 5/12/17.
 */

public class SConnectInputStream extends InputStream {
    InputStream is;
    MyCallBack prgressUpdater;
    long finishSize= 0;
    boolean shouldEncrypt = true;

    public boolean isShouldEncrypt() {
        return shouldEncrypt;
    }

    public void setShouldEncrypt(boolean shouldEncrypt) {
        this.shouldEncrypt = shouldEncrypt;
    }

    public SConnectInputStream(InputStream is) {
        this.is = is;
    }

    public void setPrgressUpdater(MyCallBack prgressUpdater) {
        this.prgressUpdater = prgressUpdater;
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

        if (shouldEncrypt) {
            for (int i = off; i < off + len; i++) {
                b[i] = (byte) (b[i] ^ 2);
            }
        }
        finishSize +=len;
        if (prgressUpdater != null)
            prgressUpdater.callback(EventConst.PROGRESS_UPDATE,1, finishSize);

        return n;
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.is.close();
    }
}
