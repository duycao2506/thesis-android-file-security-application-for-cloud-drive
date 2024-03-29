package thesis.tg.com.s_cloud.data.from_third_party.task;

import android.os.AsyncTask;

import com.dropbox.core.DbxException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.utils.EventBroker;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by admin on 5/20/17.
 */

public class DownloadTask extends TransferTask {

    private int type;
    public DownloadTask(BaseApplication ba) {
        super(ba);
    }

    @Override
    protected void transfer() throws IOException, DbxException, NoSuchAlgorithmException {
        super.transfer();
    }

    @Override
    protected void afterTransfer(Boolean aVoid) {
        super.afterTransfer(aVoid);
        if (aVoid)
            EventBroker.getInstance(ba).publish(EventConst.FINISH_DOWNLOADING, this.to, this.file);
        else
            EventBroker.getInstance(ba).publish(EventConst.FAIL_TRANSFER, this.to, this.file);
    }

}
