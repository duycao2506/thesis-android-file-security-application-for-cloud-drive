package thesis.tg.com.s_cloud.data.from_third_party;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;

/**
 * Created by admin on 5/6/17.
 */

public abstract class DriveWrapper {
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

}
