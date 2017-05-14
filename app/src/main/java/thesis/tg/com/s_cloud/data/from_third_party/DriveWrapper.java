package thesis.tg.com.s_cloud.data.from_third_party;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DriveType;

/**
 * Created by admin on 5/6/17.
 */

public class DriveWrapper {

    protected static GoogleDriveWrapper gdinstance;
    protected static DropboxDriveWrapper dbinstance;

    public static DriveWrapper getInstance(int drivetype) {
        switch (drivetype){
            case DriveType.GOOGLE:
                return GoogleDriveWrapper.getInstance();
            case DriveType.DROPBOX:
                return DropboxDriveWrapper.getInstance();
            default:
                return new DriveWrapper();
        }
    }

    public void getFilesByFolderId(boolean isMore, MyCallBack callBack){

    }

    /**
     * Upload
     * @param fileId
     * @param caller
     */
    public void transferFrom(String fileId, MyCallBack caller){

    }

    /**
     * Download
     * @param file
     * @param caller
     */
    public void transferTo(SDriveFile file, MyCallBack caller){

    }

    public void addNewListFileTask(String folderId){

    }

    public void popListFileTask(){

    }

    public void resetListFileTask(){

    }

}
