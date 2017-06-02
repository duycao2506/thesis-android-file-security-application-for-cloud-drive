package thesis.tg.com.s_cloud.data.from_third_party.dropbox;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.UploadSessionCursor;
import com.dropbox.core.v2.files.UploadUploader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;

import thesis.tg.com.s_cloud.data.from_third_party.task.UploadTask;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.utils.DataUtils;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.SConnectInputStream;

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
    protected void transfer() throws IOException, DbxException {
        super.transfer();
        UploadUploader uu = dbxClientV2.files().upload(file.getFolder()+"/"+file.getName());
        OutputStream os = uu.getOutputStream();
        SequenceInputStream inputStream =
                new SequenceInputStream(new ByteArrayInputStream(
                        DataUtils.getDataHeader()),
                        new SConnectInputStream(file.getInputstream()));
        byte[] buffer = new byte[2048];
        int numRead = 0;
        while ((numRead = inputStream.read(buffer)) >= 0) {
            os.write(buffer, 0, numRead);
        }
        os.write(buffer);
        os.close();
        inputStream.close();
        uu.finish();
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
        return DriveType.DROPBOX;
    }


}
