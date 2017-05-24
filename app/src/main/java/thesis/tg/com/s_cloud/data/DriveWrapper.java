package thesis.tg.com.s_cloud.data;

import thesis.tg.com.s_cloud.data.from_local.LocalDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.dropbox.DbxDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleDriveWrapper;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DriveType;

/**
 * Created by admin on 5/6/17.
 */

public class DriveWrapper {

    protected static GoogleDriveWrapper gdinstance;
    protected static DbxDriveWrapper dbinstance;
    protected static LocalDriveWrapper localDriveWrapper;

    public static DriveWrapper getInstance(int drivetype) {
        switch (drivetype){
            case DriveType.GOOGLE:
                return GoogleDriveWrapper.getInstance();
            case DriveType.DROPBOX:
                return DbxDriveWrapper.getInstance();
            case DriveType.LOCAL:
                return LocalDriveWrapper.getInstance();
            default:
                return new DriveWrapper();
        }
    }

    public void getFilesInTopFolder(boolean isMore, MyCallBack callBack){

    }


    public void addNewListFileTask(String folderId){

    }

    public void popListFileTask(){

    }

    public void resetListFileTask(){

    }

}
