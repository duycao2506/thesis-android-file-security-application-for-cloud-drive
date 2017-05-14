package thesis.tg.com.s_cloud.data.from_third_party;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.SFileInputStream;

/**
 * Created by admin on 5/12/17.
 */

public class DrivesManager {

    private DrivesManager(){

    }

    private static DrivesManager instance;

    public static DrivesManager getInstance(){
        if (instance == null)
            instance = new DrivesManager();
        return instance;
    }

    public void transferDataTo(int driveType, SDriveFile data, boolean sync){
        switch (driveType){
            case DriveType.LOCAL:
                new GoogleDownloadTask(GoogleDriveWrapper.getInstance().getDriveService()).start(data);
                break;

            case DriveType.GOOGLE:
                break;

            case DriveType.DROPBOX:
                break;
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


}
