package thesis.tg.com.s_cloud.data.from_third_party.google_drive;

import android.util.Log;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import thesis.tg.com.s_cloud.data.from_third_party.task.FileListingTask;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.entities.SDriveFolder;
import thesis.tg.com.s_cloud.utils.DriveType;

/**
 * Created by admin on 5/8/17.
 */

public class GoogleListFileTask extends FileListingTask{
    Drive driveService;

    public GoogleListFileTask(Drive driveService, String folderId) {
        super(folderId);
        this.driveService = driveService;
    }


    @Override
    protected List<SDriveFile> getDataFromApi() throws IOException {
        // Get a list of up to 10 files.
        if (nextPageToken.compareTo(END_TOKEN) == 0)
            return new ArrayList<>();
        Drive.Files.List tmpList = driveService.files().list()
                .setFields("nextPageToken, files(id, name, originalFilename, mimeType, size, modifiedTime, fullFileExtension, capabilities(canListChildren))");
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
            boolean isFolder = file.getCapabilities().getCanListChildren();
            SDriveFile sDriveFile = isFolder? new SDriveFolder() : new SDriveFile();
            sDriveFile.setLastModifiedDate(file.getModifiedTime().toString());
            sDriveFile.setId(file.getId());
            sDriveFile.setName(file.getOriginalFilename() == null ? file.getName() : file.getOriginalFilename());
            sDriveFile.setFileSize(file.getSize() == null ? -1 : file.getSize());
            sDriveFile.setMimeType(file.getMimeType());
            sDriveFile.setExtension(file.getFullFileExtension());

            Log.d("EXTENSION and MIME:", sDriveFile.getExtension() +"//" + sDriveFile.getMimeType());
            sDriveFile.setFolder(folderId);
            sDriveFile.setCloud_type(DriveType.GOOGLE);
            if (isFolder)
                sDriveFiles.add(0,sDriveFile);
            else
                sDriveFiles.add(sDriveFile);
        }
        return sDriveFiles;
    }


}
