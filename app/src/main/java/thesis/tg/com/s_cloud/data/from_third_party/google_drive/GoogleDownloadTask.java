package thesis.tg.com.s_cloud.data.from_third_party.google_drive;

import android.os.AsyncTask;

import com.google.api.services.drive.Drive;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import thesis.tg.com.s_cloud.data.from_third_party.task.DownloadTask;
import thesis.tg.com.s_cloud.entities.SDriveFile;
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

    public GoogleDownloadTask(Drive driveService, int toDriveType) {
        super();
        this.driveService = driveService;
        this.from = DriveType.GOOGLE;
        this.to = toDriveType;
    }

    @Override
    protected void transfer() {
        super.transfer();
        SConnectOutputstream outputStream = null;
        try {
            OutputStream fos = file.getOutputStream(this.to,file.getName());
            outputStream = new SConnectOutputstream(DataUtils.getDataHeader(), fos);
            driveService.files().get(file.getId())
                    .executeMediaAndDownloadTo(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getType() {
        return DriveType.GOOGLE;
    }
}
