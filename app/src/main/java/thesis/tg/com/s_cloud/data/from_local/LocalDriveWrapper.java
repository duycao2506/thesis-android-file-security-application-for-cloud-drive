package thesis.tg.com.s_cloud.data.from_local;

import thesis.tg.com.s_cloud.data.DriveWrapper;

/**
 * Created by admin on 5/20/17.
 */

public class LocalDriveWrapper extends DriveWrapper {
    private LocalDriveWrapper(){

    }

    public static LocalDriveWrapper getInstance(){
        if (localDriveWrapper == null)
            localDriveWrapper = new LocalDriveWrapper();
        return localDriveWrapper;
    }
}
