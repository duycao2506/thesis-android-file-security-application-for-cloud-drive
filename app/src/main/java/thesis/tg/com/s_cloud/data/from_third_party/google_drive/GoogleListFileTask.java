package thesis.tg.com.s_cloud.data.from_third_party.google_drive;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Channel;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.entities.SDriveFolder;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by admin on 5/8/17.
 */

public class GoogleListFileTask {
    private static final String END_TOKEN = "end";
    String nextPageToken = "";
    String folderId = "";
    ListFileTask listFileTask;

    public GoogleListFileTask(String folderId) {
        this.nextPageToken = "";
        this.folderId = folderId;
    }

    public void getMoreList(Drive driveService, MyCallBack caller)
    {
        listFileTask = new ListFileTask(driveService, caller);
        listFileTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void refreshList(Drive driveService, MyCallBack caller){
        nextPageToken = "";
        getMoreList(driveService, caller);
    }


    public ArrayList<SDriveFile> toSDriveFile(List<File> driveFiles){
        ArrayList<SDriveFile> sDriveFiles = new ArrayList<>();
        for (File file : driveFiles){
            SDriveFile sDriveFile = file.getCapabilities().getCanListChildren()? new SDriveFolder() : new SDriveFile();

            sDriveFile.setCreatedDate(file.getCreatedTime().toString());
            sDriveFile.setId(file.getId());
            sDriveFile.setName(file.getOriginalFilename() == null ? file.getName() : file.getOriginalFilename());
            sDriveFile.setFileSize(file.getSize() == null ? -1 : file.getSize());
            sDriveFile.setMimeType(file.getMimeType());
            sDriveFile.setExtension(file.getFullFileExtension());
            sDriveFile.setCloud_type(DriveType.GOOGLE);
            sDriveFiles.add(sDriveFile);
        }
        return sDriveFiles;
    }


    private class ListFileTask extends AsyncTask<Void, Void, List<SDriveFile>> {
        private MyCallBack caller;
        private com.google.api.services.drive.Drive mService = null;
        private Exception mLastError = null;
        private ProgressDialog mProgress;




        ListFileTask(Drive driveService, MyCallBack caller) {
            this.caller = caller;
            this.mService = driveService;
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
        private List<SDriveFile> getDataFromApi() throws IOException {

            // Get a list of up to 10 files.
            if (nextPageToken.compareTo(END_TOKEN) == 0)
                return new ArrayList<>();
            Drive.Files.List tmpList = mService.files().list()
                    .setPageSize(10)
                    .setFields("nextPageToken, files(id, name, originalFilename, mimeType, size, createdTime, fullFileExtension, capabilities(canListChildren))");
            if (nextPageToken != null && !nextPageToken.isEmpty())
                tmpList = tmpList.setPageToken(nextPageToken);
            if (folderId != null && !folderId.isEmpty())
                tmpList = tmpList.setQ("'" + folderId + "' in parents and trashed=false");

            FileList result = tmpList.execute();
            List<File> files = result.getFiles();


            if (result.getNextPageToken() == null || result.getNextPageToken().length() == 0) {
                nextPageToken = END_TOKEN;
            }else
                nextPageToken = result.getNextPageToken();


            if (files != null) {
                return toSDriveFile(files);
            }
            return null;
        }





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
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
//                    showGooglePlayServicesAvailabilityErrorDialog(
//                            ((GooglePlayServicesAvailabilityIOException) mLastError)
//                                    .getConnectionStatusCode());
                    //TODO: ERROR Notice
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
//                    startActivityForResult(
//                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
//                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
//                    mOutputText.setText("The following error occurred:\n"
//                            + mLastError.getMessage());
                }
            } else {
//                mOutputText.setText("Request cancelled.");?
            }
        }
    }


}
