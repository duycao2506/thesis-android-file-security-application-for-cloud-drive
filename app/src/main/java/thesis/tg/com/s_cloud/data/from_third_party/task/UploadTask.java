package thesis.tg.com.s_cloud.data.from_third_party.task;

import android.os.AsyncTask;

import com.dropbox.core.DbxException;

import java.io.IOException;
import java.io.InputStream;

import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.utils.EventBroker;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by admin on 5/20/17.
 */

public class UploadTask extends TransferTask {
    public UploadTask() {
        super();
    }

    @Override
    protected void transfer() throws IOException, DbxException{
        super.transfer();
    }

    @Override
    protected void afterTransfer() {
        super.afterTransfer();
        EventBroker.getInstance().publish(EventConst.FINISH_UPLOADING, getType(),file.getLink());
    }

    public void start(SDriveFile data){
        this.file = data;
        at.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
