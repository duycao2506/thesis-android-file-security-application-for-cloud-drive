package thesis.tg.com.s_cloud.entities;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by admin on 5/20/17.
 */

public class SyncSDriveFile extends SDriveFile {
    ArrayList<String> cloudids;


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
