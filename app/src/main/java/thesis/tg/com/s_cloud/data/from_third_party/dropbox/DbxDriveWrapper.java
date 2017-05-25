package thesis.tg.com.s_cloud.data.from_third_party.dropbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;

import thesis.tg.com.s_cloud.data.CloudDriveWrapper;
import thesis.tg.com.s_cloud.entities.DriveUser;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by CKLD on 5/14/17.
 */

public class DbxDriveWrapper extends CloudDriveWrapper {
    private DbxClientV2 dbxClientV2;

    private DbxDriveWrapper(){

    }



    public static DbxDriveWrapper getInstance(){
        if (dbinstance == null)
            dbinstance = new DbxDriveWrapper();
        return dbinstance;
    }

    public void signIn(final Context context, final MyCallBack caller) {
        final String accessToken = Auth.getOAuth2Token(); //generate Access Token
        if (accessToken != null) {
            new AsyncTask<Void, Void, String>(){
                @Override
                protected String doInBackground(Void... params) {
                    SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
                    prefs.edit().putString("access-token", accessToken).apply();

                    DbxRequestConfig dbxRequestConfig = DbxRequestConfig.newBuilder("scloud/dgproduction").build();
                    dbxClientV2 = new DbxClientV2(dbxRequestConfig,accessToken);

                    //GEt ACcount;
                    FullAccount fa = null;
                    try {
                        fa = dbxClientV2.users().getCurrentAccount();
                    } catch (DbxException e) {
                        e.printStackTrace();
                    }
                    if (fa != null){
                        DriveUser user = DriveUser.getInstance();
                        user.setDropbox_id(fa.getAccountId());
                        if (user.isSignedIn()){
                            caller.callback(EventConst.ADD_DRIVE,DriveType.DROPBOX, null);
                            //TODO: send accesstoken to add drive on server
                            return EventConst.ADD_DRIVE;
                        }

                        //TODO: send accesstoken to get info from server
                        user.setEmail(fa.getEmail());
                        user.setName(fa.getName().getDisplayName());
                        user.setAvatarLink(fa.getProfilePhotoUrl());
                        user.setCountry(fa.getCountry());
                        return EventConst.LOGIN_SUCCESS;
                    }
                    return EventConst.LOGIN_FAIL;
                }

                @Override
                protected void onPostExecute(String s) {
                    caller.callback(s, DriveType.DROPBOX, null);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            //Store accessToken in SharedPreferences


            return;
        }
        caller.callback(EventConst.LOGIN_FAIL, DriveType.DROPBOX, null);
    }

    @Override
    public void resetListFileTask() {
        super.resetListFileTask();
    }

    @Override
    public void getFilesInTopFolder(boolean isMore, MyCallBack callBack) {
        super.getFilesInTopFolder(isMore, callBack);
    }

    @Override
    public void addNewListFileTask(String folderId) {
        super.addNewListFileTask(folderId);
    }

    @Override
    public void popListFileTask() {
        super.popListFileTask();
    }

    public DbxClientV2 getClient() {
        return dbxClientV2;
    }
}
