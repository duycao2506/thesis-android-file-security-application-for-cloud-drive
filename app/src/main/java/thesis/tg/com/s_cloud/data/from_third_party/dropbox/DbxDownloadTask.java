package thesis.tg.com.s_cloud.data.from_third_party.dropbox;

import thesis.tg.com.s_cloud.data.from_third_party.task.DownloadTask;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.utils.DriveType;

/**
 * Created by admin on 5/21/17.
 */

public class DbxDownloadTask extends DownloadTask {
    public DbxDownloadTask() {
        super();
    }

    @Override
    protected void transfer() {
        super.transfer();

    }

    @Override
    public int getType() {
        return DriveType.DROPBOX;
    }

    @Override
    public void start(SDriveFile file) {

        super.start(file);
    }
}
