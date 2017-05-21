package thesis.tg.com.s_cloud.data.from_third_party.google_drive;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContentResolverCompat;
import android.webkit.MimeTypeMap;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.net.URI;

import thesis.tg.com.s_cloud.data.from_third_party.task.UploadTask;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.utils.EventBroker;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DataUtils;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;
import thesis.tg.com.s_cloud.utils.SConnectInputStream;

/**
 * Created by admin on 5/12/17.
 */

public class GoogleUploadTask extends UploadTask {

    Drive driveService;

    public GoogleUploadTask(Drive driveService) {
        super();
        this.driveService = driveService;
    }


    @Override
    protected void transfer() {
        super.transfer();
        DriveContentInputStream dvic = new DriveContentInputStream(
                file.getMimeType(),
                new SConnectInputStream(file.getInputstream()),
                file.getFileSize());
        com.google.api.services.drive.model.File fileCnt = new com.google.api.services.drive.model.File();

        //Getname
        fileCnt.setName(file.getName());
        fileCnt.setMimeType(file.getMimeType());
        fileCnt.setOriginalFilename(file.getName());

        try {
            driveService.files().create(fileCnt,dvic)
                    .setFields("id, name, originalFilename, mimeType")
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void afterTransfer() {
        super.afterTransfer();
    }

    @Override
    public int getType() {
        return DriveType.GOOGLE;
    }


    class DriveContentInputStream extends AbstractInputStreamContent{
        SConnectInputStream sConnectInputStream;
        long transfersize;
        byte[] header;
        public DriveContentInputStream(String type, SConnectInputStream sFileInputStream,long transferSize) {
            super(type);
            this.sConnectInputStream = sFileInputStream;
            this.transfersize = transferSize;
        }




        @Override
        public InputStream getInputStream() throws IOException {
            if (header == null )
                return sConnectInputStream;

            SequenceInputStream inputStream =
                    new SequenceInputStream(new ByteArrayInputStream(
                                                    DataUtils.getDataHeader()),
                                                    sConnectInputStream);
            return sConnectInputStream;
        }

        @Override
        public long getLength() throws IOException {
            return transfersize;
        }

        @Override
        public boolean retrySupported() {
            return false;
        }
    }
}
