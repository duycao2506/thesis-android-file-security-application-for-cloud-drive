package thesis.tg.com.s_cloud.data;

import java.util.ArrayList;

import thesis.tg.com.s_cloud.data.from_local.LocalDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.dropbox.DbxDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleListFileTask;
import thesis.tg.com.s_cloud.data.from_third_party.task.FileListingTask;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DriveType;

/**
 * Created by admin on 5/6/17.
 */

public class CloudDriveWrapper {

    protected static GoogleDriveWrapper gdinstance;
    protected static DbxDriveWrapper dbinstance;
    protected static LocalDriveWrapper localDriveWrapper;
    protected ArrayList<FileListingTask> glftList;

    public static CloudDriveWrapper getInstance(int drivetype) {
        switch (drivetype){
            case DriveType.GOOGLE:
                return GoogleDriveWrapper.getInstance();
            case DriveType.DROPBOX:
                return DbxDriveWrapper.getInstance();
            case DriveType.LOCAL:
                return LocalDriveWrapper.getInstance();
            default:
                return new CloudDriveWrapper();
        }
    }

    public void getFilesInTopFolder(boolean isMore, MyCallBack callBack){

    }


    public void addNewListFileTask(String folderId){
        if (this.glftList == null)
            glftList = new ArrayList<>();
    }

    public void popListFileTask(){
        this.glftList.remove(this.glftList.size()-1);
    }

    public void resetListFileTask(){
        this.glftList = new ArrayList<>();
    }


    public void signOut() {

    }
}
