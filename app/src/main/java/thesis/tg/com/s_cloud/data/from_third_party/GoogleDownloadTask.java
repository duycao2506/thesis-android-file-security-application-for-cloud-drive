package thesis.tg.com.s_cloud.data.from_third_party;

import android.os.AsyncTask;

import com.google.api.services.drive.Drive;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.utils.EventBroker;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;
import thesis.tg.com.s_cloud.utils.SFileOutputStream;

/**
 * Created by admin on 5/12/17.
 */

public class GoogleDownloadTask {
    Drive driveService;
    MyCallBack caller;
    private SDriveFile file;

    public GoogleDownloadTask(Drive driveService) {
        this.driveService = driveService;
    }

    AsyncTask<String,Void,Void> at = new AsyncTask<String, Void, Void>(){
        @Override
        protected Void doInBackground(String...params) {
            File root = android.os.Environment.getExternalStorageDirectory();
            File ckld = new File(root,file.getName());

            OutputStream outputStream = null;
            try {
                outputStream = new SFileOutputStream(ckld);
                driveService.files().get(params[0])
                        .executeMediaAndDownloadTo(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            EventBroker.getInstance().publish(EventConst.FINISH_DOWNLOADING, DriveType.GOOGLE, file);
        }
    };

    public void start(SDriveFile file){
        this.file = file;
        at.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,file.getId());

    }
}
