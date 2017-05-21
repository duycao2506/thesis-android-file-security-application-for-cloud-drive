package thesis.tg.com.s_cloud.data.from_third_party.dropbox;

import thesis.tg.com.s_cloud.data.DriveWrapper;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;

/**
 * Created by admin on 5/14/17.
 */

public class DbxDriveWrapper extends DriveWrapper {
    private DbxDriveWrapper(){

    }



    public static DbxDriveWrapper getInstance(){
        if (dbinstance == null)
            dbinstance = new DbxDriveWrapper();
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
    public void addNewListFileTask(String folderId) {
        super.addNewListFileTask(folderId);
    }

    @Override
    public void popListFileTask() {
        super.popListFileTask();
    }
}
