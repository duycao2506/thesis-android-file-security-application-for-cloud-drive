package thesis.tg.com.s_cloud.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import java.io.File;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.from_third_party.dropbox.DbxDownloadTask;
import thesis.tg.com.s_cloud.data.from_third_party.dropbox.DbxDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.dropbox.DbxUploadTask;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleDownloadTask;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleUploadTask;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DriveType;

/**
 * Created by CKLD on 5/12/17.
 */

public class DrivesManager {
    private int numOfDrives = 3;
    private DrivesManager(){

    }

    private static DrivesManager instance;

    public static DrivesManager getInstance(){
        if (instance == null)
            instance = new DrivesManager();
        return instance;
    }

    //from Drive having in the data
    public void transferDataTo(int driveType, SDriveFile data, boolean sync){
        switch (driveType){
            case DriveType.LOCAL:
                if (data.getCloud_type() == DriveType.DROPBOX)
                    new DbxDownloadTask();
            case DriveType.DROPBOX:
                if (data.getCloud_type() == DriveType.GOOGLE)
                    new GoogleDownloadTask(GoogleDriveWrapper.getInstance().getDriveService(),driveType).start(data);
                if (data.getCloud_type() == DriveType.LOCAL)
                    new DbxUploadTask(DbxDriveWrapper.getInstance().getClient());
                break;
            case DriveType.GOOGLE:
                new GoogleUploadTask(GoogleDriveWrapper.getInstance().getDriveService()).start(data);
                break;
        }
    }

    public void autoSignIn(Context context, final MyCallBack caller){
        //Dropbox
        DbxDriveWrapper.getInstance().signIn(context, caller);



        //Sign int automatically first
        final GoogleApiClient gac = GoogleDriveWrapper
                .getInstance()
                .googleSignInServiceInit(context);

        OptionalPendingResult<GoogleSignInResult> op = Auth.GoogleSignInApi.silentSignIn(gac);
        op.setResultCallback(new ResultCallback<GoogleSignInResult>() {
            @Override
            public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                GoogleDriveWrapper.getInstance()
                        .handleSignInResult(googleSignInResult, caller);
            }
        });
    }

    public String getDriveName(Context context, int id){
        switch (id){
            case DriveType.DROPBOX:
                return context.getString(R.string.dbox);
            case DriveType.GOOGLE:
                return context.getString(R.string.g_drive);
            case DriveType.LOCAL:
                return context.getString(R.string.local_storage);
            default:
                return context.getString(R.string.app_name);
        }
    }


    public int getNumDrive() {
        return numOfDrives;
    }

    public String getLocalPath() {
        File localfolder = android.os.Environment.getExternalStorageDirectory();
        File appFolder = new File(localfolder, "S-Cloud");
        if (!appFolder.exists())
            appFolder.mkdir();
        return appFolder.getPath();
    }
}
