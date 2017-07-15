package thesis.tg.com.s_cloud.data.from_third_party.google_drive;

import com.dropbox.core.DbxException;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.services.drive.Drive;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;

import thesis.tg.com.s_cloud.data.from_third_party.task.UploadTask;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.utils.DataUtils;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.SConnectInputStream;

/**
 * Created by admin on 5/12/17.
 */

public class GoogleUploadTask extends UploadTask {

    Drive driveService;
    public GoogleUploadTask(Drive driveService, BaseApplication ba) {
        super(ba);
        this.driveService = driveService;
    }


    @Override
    protected void transfer() throws IOException, DbxException{
        super.transfer();
        SConnectInputStream scis = new SConnectInputStream(file.getInputstream(ba));
        scis.setPrgressUpdater(at);
        DriveContentInputStream dvic = new DriveContentInputStream(
                file.getMimeType(),
                scis,
                file.getFileSize());

        if (from == DriveType.LOCAL || from == DriveType.LOCAL_STORAGE) {
            dvic.setHeader(DataUtils.getDataHeader());
        }else{
            scis.setShouldEncrypt(false);
        }

        com.google.api.services.drive.model.File fileCnt = new com.google.api.services.drive.model.File();

        //Getname
        //TODO: MORE is choosing folder
        boolean isRoot = file.getFolder().length() == 0 || from != DriveType.LOCAL_STORAGE;

        if (!isRoot){
            ArrayList<String> a = new ArrayList<String>();
            a.add(file.getFolder());
            fileCnt.setParents(a);
        }
        fileCnt.setName(file.getName());
        fileCnt.setMimeType(file.getMimeType());
        fileCnt.setOriginalFilename(file.getName());

        driveService.files().create(fileCnt,dvic)
                .setFields("id, name, originalFilename, mimeType" + (isRoot? "" : ", parents"))
                .execute();
    }

    @Override
    protected void afterTransfer(Boolean aVoid) {
        super.afterTransfer(aVoid);
    }

    @Override
    public int getType() {
        return DriveType.GOOGLE;
    }


    class DriveContentInputStream extends AbstractInputStreamContent{
        SConnectInputStream sConnectInputStream;
        long transfersize;
        byte[] header;


        public void setHeader(byte[] header) {
            this.header = header;
        }

        public DriveContentInputStream(String type, SConnectInputStream sFileInputStream, long transferSize) {
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
                                                    header),
                                                    sConnectInputStream);
            return inputStream;
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
