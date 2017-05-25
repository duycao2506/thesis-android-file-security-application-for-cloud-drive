package thesis.tg.com.s_cloud.data.from_third_party.dropbox;

import com.dropbox.core.v2.DbxClientV2;

import thesis.tg.com.s_cloud.data.from_third_party.task.UploadTask;
import thesis.tg.com.s_cloud.entities.SDriveFile;

/**
 * Created by admin on 5/21/17.
 */

public class DbxUploadTask extends UploadTask {
    DbxClientV2 dbxClientV2;

    public DbxUploadTask(DbxClientV2 client) {
        super();
        this.dbxClientV2 = client;
    }

    @Override
    protected void transfer() {
        super.transfer();

    }

    @Override
    protected void afterTransfer() {
        super.afterTransfer();
    }

    @Override
    public void start(SDriveFile data) {
        super.start(data);
    }

    @Override
    public int getType() {
        return super.getType();
    }


}
