package thesis.tg.com.s_cloud.data.from_third_party;

import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;

/**
 * Created by admin on 5/14/17.
 */

public class DropboxDriveWrapper extends DriveWrapper {
    private DropboxDriveWrapper(){

    }



    public static DropboxDriveWrapper getInstance(){
        if (dbinstance == null)
            dbinstance = new DropboxDriveWrapper();
        return dbinstance;
    }

    @Override
    public void resetListFileTask() {
        super.resetListFileTask();
    }

    @Override
    public void getFilesByFolderId(boolean isMore, MyCallBack callBack) {
        super.getFilesByFolderId(isMore, callBack);
    }

    @Override
    public void transferFrom(String fileId, MyCallBack caller) {
        super.transferFrom(fileId, caller);
    }

    @Override
    public void transferTo(SDriveFile file, MyCallBack caller) {
        super.transferTo(file, caller);
    }

    @Override
    public void addNewListFileTask(String folderId) {
        super.addNewListFileTask(folderId);
    }

    @Override
    public void popListFileTask() {
        super.popListFileTask();
    }
}
