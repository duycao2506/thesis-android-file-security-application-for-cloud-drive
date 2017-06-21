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
import thesis.tg.com.s_cloud.data.from_local.LocalImportTask;
import thesis.tg.com.s_cloud.data.from_third_party.dropbox.DbxDownloadTask;
import thesis.tg.com.s_cloud.data.from_third_party.dropbox.DbxDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.dropbox.DbxUploadTask;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleDownloadTask;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleUploadTask;
import thesis.tg.com.s_cloud.data.from_third_party.task.TransferTask;
import thesis.tg.com.s_cloud.entities.DriveUser;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.ResourcesUtils;

/**
 * Created by CKLD on 5/12/17.
 */

public class DrivesManager {


    private BaseApplication ba;
    private int numOfDrives = 3;
    private int[] loggedInDrives ;



    private DrivesManager(){
        loggedInDrives = new int[numOfDrives-1];
        for (int i = 0; i < numOfDrives-1;i++)
            loggedInDrives[i] = 0;
    }


    public static DrivesManager create(BaseApplication application){
        DrivesManager drivesManager = application.getDriveMannager();
        if (drivesManager == null) {
            drivesManager = new DrivesManager();
            drivesManager.ba = application;
        }
        return drivesManager;
    }

    //from Drive having in the data
    public void transferDataTo(int driveType, SDriveFile data, boolean sync){
        TransferTask tt = null;
        switch (driveType){
            case DriveType.LOCAL:
                if (data.getCloud_type() == DriveType.DROPBOX)
                    tt = new DbxDownloadTask(
                            ((DbxDriveWrapper)ba.getDriveWrapper(DriveType.DROPBOX))
                                    .getClient(),ba);
                else if (data.getCloud_type() == DriveType.GOOGLE)
                    tt = new GoogleDownloadTask(
                            ((GoogleDriveWrapper) ba.getDriveWrapper(DriveType.GOOGLE))
                                    .getDriveService(),ba);
                else {
                    tt = new LocalImportTask(ba);
                }
                if (tt== null) return;
                break;
            case DriveType.DROPBOX:
                tt = new DbxUploadTask(
                        ((DbxDriveWrapper)ba.getDriveWrapper(DriveType.DROPBOX))
                        .getClient(),ba);
                break;
            case DriveType.GOOGLE:
                tt = new GoogleUploadTask(
                        ((GoogleDriveWrapper) ba.getDriveWrapper(DriveType.GOOGLE))
                        .getDriveService(),ba);
                break;
            default:
                return;
        }
        tt.setFrom(data.getCloud_type());
        tt.setTo(driveType);
        tt.start(data);
    }

    public void autoSignIn(Context context, final MyCallBack caller){
        autoSignInDropbox(context, caller);
        autoSignInGoogle(context, caller);
    }

    public void autoSignInDropbox(Context context, MyCallBack caller){
        //Dropbox
        DbxDriveWrapper dbxDriveWrapper = ((DbxDriveWrapper)ba.getDriveWrapper(DriveType.DROPBOX));
        if (!DriveUser.getInstance().isSignedIn(DriveType.DROPBOX))
            dbxDriveWrapper.signIn(context, caller);
    }

    public void autoSignInGoogle(Context context, final MyCallBack caller){
        //Sign int automatically first
        final GoogleDriveWrapper googleDriveWrapper = (GoogleDriveWrapper) ba.getDriveWrapper(DriveType.GOOGLE);
        if (!DriveUser.getInstance().isSignedIn(DriveType.GOOGLE)) {
            GoogleApiClient gac = null;
            gac =  googleDriveWrapper.getClient();
            if (gac == null)
                gac = googleDriveWrapper.googleSignInServiceInit(context);

            OptionalPendingResult<GoogleSignInResult> op = Auth.GoogleSignInApi.silentSignIn(gac);
            op.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    googleDriveWrapper.handleSignInResult(googleSignInResult, caller);
                }
            });
        }
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

    public void setSuccessLogin(int driveType){
        loggedInDrives[ba.getResourcesUtils().getIndexByType(driveType)] = 1;
    }

    public void setFailLogin(int driveType){
        loggedInDrives[ba.getResourcesUtils().getIndexByType(driveType)] = -1;
    }

    public boolean isTriedLoginAll(){
        for (int i : loggedInDrives){
            if (i == 0)
                return false;
        }
        return true;
    }


    public void refreshLoginAttemps() {
        for (int i = 0 ; i < loggedInDrives.length; i ++){
            loggedInDrives[i] = 0;
        }
    }
}
