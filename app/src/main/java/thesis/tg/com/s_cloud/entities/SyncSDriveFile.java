package thesis.tg.com.s_cloud.entities;

import java.io.File;
import java.sql.DriverManager;
import java.util.ArrayList;

import thesis.tg.com.s_cloud.data.DrivesManager;

/**
 * Created by admin on 5/20/17.
 */

public class SyncSDriveFile extends SDriveFile {
    String[] cloudids;

    public SyncSDriveFile(int numDrives) {
        cloudids = new String[numDrives];
    }

    public SyncSDriveFile(File file, String mimeType, int numDrives) {
        super(file, mimeType);
        cloudids = new String[numDrives];
    }

    /**
     * for synced file instance
     * @return
     */

    public int getCloud_status() {
        return cloud_type;
    }

    public void turnCloudStatus(int dt) {
        this.cloud_type ^= (1 << dt);
    }
    //////



}
