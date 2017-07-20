package thesis.tg.com.s_cloud.data.from_third_party.google_drive;

import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.google.api.services.drive.Drive;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

import thesis.tg.com.s_cloud.data.from_third_party.task.DownloadTask;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.utils.EventBroker;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DataUtils;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;
import thesis.tg.com.s_cloud.utils.SConnectOutputstream;

/**
 * Created by admin on 5/12/17.
 */

public class GoogleDownloadTask extends DownloadTask {
    Drive driveService;

    public GoogleDownloadTask(Drive driveService, BaseApplication ba) {
        super(ba);
        this.driveService = driveService;
        this.from = DriveType.GOOGLE;
    }

    @Override
    protected void transfer() throws IOException, DbxException, NoSuchAlgorithmException {
        super.transfer();
        SConnectOutputstream outputStream = null;

        OutputStream fos = file.getOutputStream(this.to,file.getName(), ba);
        outputStream = new SConnectOutputstream(ba, fos);
        outputStream.setPrgresslistenner(at);
        InputStream is = driveService.files().get(file.getId())
                .executeMediaAsInputStream();
        byte[] buffer = new byte[2048];
        int numRead = 0;
        while ((numRead = is.read(buffer,0,2048)) >= 0) {
            outputStream.write(buffer, 0, numRead);
        }
        is.close();
        outputStream.close();

    }



    @Override
    public int getType() {
        return DriveType.GOOGLE;
    }
}
