package thesis.tg.com.s_cloud.data.from_third_party.dropbox;

import android.util.Log;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.util.ArrayList;
import java.util.List;

import thesis.tg.com.s_cloud.data.from_third_party.task.FileListingTask;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.entities.SDriveFolder;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DriveType;

/**
 * Created by admin on 5/21/17.
 */

public class DbxFilelistTask extends FileListingTask{
    DbxClientV2 dbxClientV2;

    public DbxFilelistTask(String folderId, DbxClientV2 dbxClientV2) {
        super(folderId);
        this.dbxClientV2 = dbxClientV2;
    }

    @Override
    public void getMoreList(MyCallBack caller) {
        super.getMoreList(caller);
    }

    @Override
    public void refreshList(MyCallBack caller) {
        super.refreshList(caller);
    }

    @Override
    public ArrayList<SDriveFile> toSDriveFile(List driveFiles) {
        ArrayList<SDriveFile> sDriveFiles = new ArrayList<>();
        for (Object obj : driveFiles){
            Metadata md = (Metadata) obj;
            FileMetadata fmd = null;
            FolderMetadata fomd =  null;
            SDriveFile sdf = null;
            if (md instanceof FileMetadata) {
                fmd = (FileMetadata) md;
                sdf = new SDriveFile();
                sdf.setId(fmd.getPathDisplay());
                sdf.setName(fmd.getName());
                sdf.setLastModifiedDate(fmd.getClientModified().toString());
                sdf.setFileSize(fmd.getSize());
                String[] tokens = fmd.getName().split("[/\\.]");
                String ext = tokens[tokens.length-1];
                sdf.setExtension(ext);
                sdf.setFolder(folderId);
                sdf.setCloud_type(DriveType.DROPBOX);
                sDriveFiles.add(sdf);
            }
            else if (md instanceof FolderMetadata) {
                fomd = (FolderMetadata) md;
                sdf = new SDriveFolder();
                sdf.setName(fomd.getName());
                sdf.setMimeType("folder");
                sdf.setId(fomd.getPathDisplay());
                sdf.setExtension("");
                sdf.setFolder(folderId);
                sdf.setCloud_type(DriveType.DROPBOX);
                sDriveFiles.add(0,sdf);
            }

            //Logger
            Log.d("ID", md.getPathDisplay()+" & "+md.getPathLower() + md.getName());
            if (fmd == null)
                Log.d("FOLDER", fomd.getId());
            else
                Log.d("FILE ME",fmd.getId());
        }
        return sDriveFiles;
    }

    @Override
    protected List<SDriveFile> getDataFromApi() throws Exception {
        if (this.nextPageToken == END_TOKEN)
            return new ArrayList< SDriveFile>();
        ListFolderResult lfr = nextPageToken.length() == 0?
                dbxClientV2.files().listFolder(folderId)
                : dbxClientV2.files().listFolderContinue(nextPageToken);
        this.nextPageToken = lfr.getHasMore()? END_TOKEN : lfr.getCursor();
        return toSDriveFile(lfr.getEntries());
    }
}
