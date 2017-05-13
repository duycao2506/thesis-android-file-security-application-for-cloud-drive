package thesis.tg.com.s_cloud.data.from_third_party;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContentResolverCompat;
import android.webkit.MimeTypeMap;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.SFileInputStream;
import thesis.tg.com.s_cloud.utils.SFileOutputStream;

/**
 * Created by admin on 5/12/17.
 */

public class GoogleUploadTask {

    Drive driveService;
    MyCallBack caller;
    private String filePath;
    private String fileType;

    public GoogleUploadTask(Drive driveService) {
        this.driveService = driveService;
    }

    AsyncTask<Void,Void,Void> at = new AsyncTask<Void, Void, Void>(){
        @Override
        protected Void doInBackground(Void...params) {
            File file = new File(filePath);
            DriveContentInputStream dvic = new DriveContentInputStream(file, fileType);
            com.google.api.services.drive.model.File fileCnt = new com.google.api.services.drive.model.File();

            //Getname
            String name = file.getName();
            int pos = name.lastIndexOf(".");
            if (pos > 0) {
                name = name.substring(0, pos);
            }
            fileCnt.setName(name);
            fileCnt.setMimeType(fileType);
            fileCnt.setOriginalFilename(file.getName());

            //GetFileExtension
//            String extension = file.getName().substring(pos+1);
//            fileCnt.setFileExtension(extension);


            try {
                driveService.files().create(fileCnt,dvic)
                        .setFields("id, name, originalFilename, mimeType")
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            caller.callback("Cool",1,null);
        }
    };


    public void start(String fileType, String filePath, MyCallBack caller){
        this.fileType = fileType;
        this.filePath = filePath;
        at.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        this.caller = caller;
    }

    class DriveContentInputStream extends AbstractInputStreamContent{
        File file;
        SFileInputStream sFileInputStream;
        public DriveContentInputStream(File file, String type) {
            super(type);
            this.file = file;
        }

        public DriveContentInputStream(String type, SFileInputStream sFileInputStream) {
            super(type);
            this.sFileInputStream = sFileInputStream;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return file != null? new SFileInputStream(file) : sFileInputStream;
        }

        @Override
        public long getLength() throws IOException {
            return file!= null? file.length() : 0;
        }

        @Override
        public boolean retrySupported() {
            return false;
        }
    }
}
