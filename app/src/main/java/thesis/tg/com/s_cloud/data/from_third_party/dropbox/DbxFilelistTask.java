package thesis.tg.com.s_cloud.data.from_third_party.dropbox;

import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import thesis.tg.com.s_cloud.data.from_third_party.task.FileListingTask;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;

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
        return super.toSDriveFile(driveFiles);
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
