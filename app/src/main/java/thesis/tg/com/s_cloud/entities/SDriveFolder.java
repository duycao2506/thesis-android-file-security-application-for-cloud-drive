package thesis.tg.com.s_cloud.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 4/26/17.
 */

public class SDriveFolder extends SDriveFile {
    private List<SDriveFile> fileList;

    public SDriveFolder() {
        fileList = new ArrayList<>();
    }

    public SDriveFolder(List<SDriveFile> fileList) {
        this.fileList = fileList;
    }
}
