package thesis.tg.com.s_cloud.data;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleDownloadTask;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleUploadTask;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.utils.DriveType;

/**
 * Created by admin on 5/12/17.
 */

public class DrivesManager {
    int numOfDrives = 2;
    boolean driveTriedSignIn[];
    private DrivesManager(){
        driveTriedSignIn = new boolean[numOfDrives];
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
                if (data.getCloud_type() == DriveType.GOOGLE)
                    new GoogleDownloadTask(GoogleDriveWrapper.getInstance().getDriveService()).start(data);
                if (data.getCloud_type() == DriveType.DROPBOX);
                break;

            case DriveType.GOOGLE:
                if (data.getCloud_type() == DriveType.LOCAL) {
                    File file = new File(data.getLink());
                    FileInputStream fis;
                    try {
                        fis = new FileInputStream(file);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return;
                    }
                    new GoogleUploadTask(GoogleDriveWrapper.getInstance().getDriveService()).start(data);
                }

                break;

            case DriveType.DROPBOX:
                break;
        }
    }

    public void autoSignIn(){

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
}
