package thesis.tg.com.s_cloud.data.from_local;

import com.dropbox.core.DbxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.NoSuchAlgorithmException;

import thesis.tg.com.s_cloud.data.from_third_party.task.TransferTask;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.utils.EventBroker;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by CKLD on 6/20/17.
 */

public class LocalImportTask extends TransferTask {
    public LocalImportTask(BaseApplication ba) {
        super(ba);
    }

    @Override
    protected void afterTransfer(Boolean aVoid) {
        super.afterTransfer(aVoid);
        if (aVoid)
            EventBroker.getInstance(ba).publish(EventConst.FINISH_UPLOADING, this.to, this.file);
        else
            EventBroker.getInstance(ba).publish(EventConst.FAIL_TRANSFER, this.to, this.file);
    }

    @Override
    protected void transfer() throws IOException, DbxException, NoSuchAlgorithmException {
        super.transfer();
        File src = new File(this.file.getId());
        File dst = new File(this.file.getFolder() + "/" + this.file.getName());
        copyFile(src, dst);
    }
    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists()) {
            boolean a = destFile.getParentFile().mkdirs();
        }

        if (!destFile.exists()) {
            boolean newFile = destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
}
