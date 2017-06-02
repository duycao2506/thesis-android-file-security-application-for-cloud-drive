package thesis.tg.com.s_cloud.data.from_third_party.dropbox;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;

import java.io.IOException;
import java.io.OutputStream;

import thesis.tg.com.s_cloud.data.from_third_party.task.DownloadTask;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.utils.DataUtils;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.SConnectOutputstream;

/**
 * Created by admin on 5/21/17.
 */

public class DbxDownloadTask extends DownloadTask {
    DbxClientV2 dbxClientV2;

    public DbxDownloadTask(DbxClientV2 client) {
        super();
        this.dbxClientV2 = client;
    }

    @Override
    protected void transfer() throws IOException, DbxException{
        super.transfer();
        SConnectOutputstream outputStream = null;

        OutputStream fos = file.getOutputStream(DriveType.LOCAL,file.getName());
        outputStream = new SConnectOutputstream(DataUtils.getDataHeader(), fos);
        dbxClientV2.files().download(file.getId())
                .download(outputStream);
    }

    @Override
    public int getType() {
        return DriveType.DROPBOX;
    }

    @Override
    public void start(SDriveFile file) {

        super.start(file);
    }
}
