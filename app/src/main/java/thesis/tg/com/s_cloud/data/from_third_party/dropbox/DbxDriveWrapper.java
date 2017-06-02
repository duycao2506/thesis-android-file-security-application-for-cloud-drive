package thesis.tg.com.s_cloud.data.from_third_party.dropbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;

import thesis.tg.com.s_cloud.data.CloudDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleListFileTask;
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
        final String accessToken = getAccessToken(context); //generate Access Token
        if (accessToken != null) {
            new AsyncTask<Void, Void, String>(){
                @Override
                protected String doInBackground(Void... params) {
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
                        if (user.isSignedIn()){
                            user.setDropbox_id(fa.getAccountId());
                            user.setDropboxEmail(fa.getEmail());
                            caller.callback(EventConst.ADD_DRIVE,DriveType.DROPBOX, null);
                            //TODO: send accesstoken to add drive on server
                            resetListFileTask();
                            return EventConst.LOGIN_SUCCESS;
                        }

                        //TODO: send accesstoken to get info from server
                        user.setDropbox_id(fa.getAccountId());
                        user.setName(fa.getName().getDisplayName());
                        user.setAvatarLink(fa.getProfilePhotoUrl());
                        user.setCountry(fa.getCountry());

                        resetListFileTask();
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
            Log.d("HELLO", "Login succ");

            return;
        }
        Log.d("HELLO", "Login fail");
        caller.callback(EventConst.LOGIN_FAIL, DriveType.DROPBOX, null);
    }

    public void saveAccToken(Context context, String token){
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        prefs.edit().putString("access-token", token).apply();
    }

    private String getAccessToken(Context context){
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), context.MODE_PRIVATE);
        String accToken = prefs.getString("access-token", null);
        return accToken;
    }

    @Override
    public void resetListFileTask() {
        super.resetListFileTask();
        addNewListFileTask("");
    }

    @Override
    public void getFilesInTopFolder(boolean isMore, MyCallBack callBack) {
        super.getFilesInTopFolder(isMore, callBack);
        if (!isMore)
            glftList.get(glftList.size() - 1).refreshList(callBack);
        else
            glftList.get(glftList.size() - 1).getMoreList(callBack);
    }

    @Override
    public void addNewListFileTask(String folderId) {
        super.addNewListFileTask(folderId);
        DbxFilelistTask glft = new DbxFilelistTask(folderId,dbxClientV2);
        glftList.add(glft);
    }

    @Override
    public void popListFileTask() {
        super.popListFileTask();
    }

    public DbxClientV2 getClient() {
        return dbxClientV2;
    }

    @Override
    public void signOut() {
        super.signOut();
        try {
            dbxClientV2.auth().tokenRevoke();
            DriveUser.getInstance().setDropbox_id(null);
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }
}
