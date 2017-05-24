package thesis.tg.com.s_cloud.data.from_third_party.google_drive;

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

import thesis.tg.com.s_cloud.data.from_third_party.task.FileListingTask;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.entities.SDriveFolder;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by admin on 5/8/17.
 */

public class GoogleListFileTask extends FileListingTask{
    Drive driveService;

    public GoogleListFileTask(String folderId) {
        super(folderId);
    }

    public void getMoreList(Drive driveService, MyCallBack caller)
    {
        this.driveService = driveService;
        listFileTask = new ListFileTask(caller);
        listFileTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void refreshList(Drive driveService, MyCallBack caller){
        nextPageToken = "";
        getMoreList(driveService, caller);
    }

    @Override
    protected List<SDriveFile> getDataFromApi() throws IOException {
        // Get a list of up to 10 files.
        if (nextPageToken.compareTo(END_TOKEN) == 0)
            return new ArrayList<>();
        Drive.Files.List tmpList = driveService.files().list()
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

    public ArrayList<SDriveFile> toSDriveFile(List driveFiles){
        ArrayList<SDriveFile> sDriveFiles = new ArrayList<>();
        for (Object obj : driveFiles){
            File file = (File) obj;
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


}
