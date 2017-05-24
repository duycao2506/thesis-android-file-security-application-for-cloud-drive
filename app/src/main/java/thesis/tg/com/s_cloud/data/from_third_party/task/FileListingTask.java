package thesis.tg.com.s_cloud.data.from_third_party.task;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleListFileTask;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.entities.SDriveFolder;
import thesis.tg.com.s_cloud.framework_components.entity.SuperObject;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by admin on 5/23/17.
 */

public class FileListingTask extends SuperObject {
    protected static final String END_TOKEN = "end";
    protected String folderId = "";
    protected ListFileTask listFileTask;
    protected String nextPageToken = "";


    public FileListingTask(String folderId) {
        this.folderId = folderId;
        this.nextPageToken = "";
    }

    public void getMoreList(MyCallBack caller)
    {
        listFileTask = new ListFileTask(caller);
        listFileTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void refreshList(MyCallBack caller){
    }


    public ArrayList<SDriveFile> toSDriveFile(List driveFiles){
        return null;
    }

    protected List<SDriveFile> getDataFromApi() throws Exception {
        return null;
    }


    protected class ListFileTask extends AsyncTask<Void, Void, List<SDriveFile>> {
        private MyCallBack caller;
        private Exception mLastError = null;
        private ProgressDialog mProgress;
        public ListFileTask(MyCallBack caller) {
            this.caller = caller;
        }
        /**
         * Background task to call Drive API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<SDriveFile> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of up to 10 file names and IDs.
         * @return List of Strings describing files, or an empty list if no files
         *         found.
         * @throws IOException
         */

        @Override
        protected void onPostExecute(List<SDriveFile> sDriveFiles) {
            if (sDriveFiles == null || sDriveFiles.size() == 0) {
                this.caller.callback(EventConst.GET_FILE_LIST,1,new ArrayList<>());
            } else {
                this.caller.callback(EventConst.GET_FILE_LIST,1,sDriveFiles);
            }
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                this.caller.callback(EventConst.GET_FILE_LIST,1,null);
            }
        }
    }
}
