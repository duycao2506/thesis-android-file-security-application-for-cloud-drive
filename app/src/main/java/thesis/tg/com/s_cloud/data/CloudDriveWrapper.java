package thesis.tg.com.s_cloud.data;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.core.DbxException;

import java.io.IOException;
import java.util.ArrayList;

import thesis.tg.com.s_cloud.data.from_local.LocalDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.dropbox.DbxDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleListFileTask;
import thesis.tg.com.s_cloud.data.from_third_party.task.FileListingTask;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by admin on 5/6/17.
 */

public class CloudDriveWrapper {
    protected ArrayList<FileListingTask> glftList;

    public static CloudDriveWrapper create(int drivetype, BaseApplication application) {
        switch (drivetype){
            case DriveType.GOOGLE:
                return GoogleDriveWrapper.getInstance(application);
            case DriveType.DROPBOX:
                return DbxDriveWrapper.getInstance(application);
            case DriveType.LOCAL:
                return LocalDriveWrapper.getInstance(application);
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


    public void signOut(MyCallBack caller) {

    }

    public void requestDelete(final String file, final MyCallBack caller){
        new AsyncTask<Void, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Void... params) {
                boolean res = false;
                try{
                    res = delete(file);
                }catch (IOException | DbxException e){
                    e.printStackTrace();
                    res = false;
                }
                return res;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                caller.callback(EventConst.DELETE_FILE,aBoolean? 1 : 0,file);
            }
        }.execute();
    }

    public void requestNewFolder(final String name, final String parent, final MyCallBack caller){
        new AsyncTask<Void, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Void... params) {
                boolean result = false;
                try {
                    result = createNewFolder(name, parent);
                } catch (DbxException | IOException e) {
                    e.printStackTrace();
                    result = false;
                }
                return result;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                caller.callback(EventConst.CREATE_FOLDER,aBoolean? 1 : 0, name);
            }
        }.execute();
    }


    /**
     * Override this function to create new folder in each cloud drive
     * @return
     */
    protected boolean createNewFolder(String name, String parent) throws DbxException, IOException {
        return false;
    }

    /**
     * Delete a file
     * @return
     */

    protected boolean delete(String file) throws DbxException, IOException{
        return false;
    }

    /**
     * Search Files and folders
     * @return
     */

    protected boolean search(String query){
        return false;
    }

    public int getType(){
        return 0;
    }

    public void updateSearchContext(String query) {
        this.glftList.get(glftList.size()-1).setSearchToken(query);
    }
}
