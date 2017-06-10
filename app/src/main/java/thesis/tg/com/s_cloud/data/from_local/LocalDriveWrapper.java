package thesis.tg.com.s_cloud.data.from_local;

import android.os.Environment;
import android.webkit.MimeTypeMap;

import com.dropbox.core.DbxException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import thesis.tg.com.s_cloud.data.CloudDriveWrapper;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.entities.SDriveFolder;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DriveType;

/**
 * Created by admin on 5/20/17.
 */

public class LocalDriveWrapper extends CloudDriveWrapper {
    Stack<File> folderStack;
    File root;
    private LocalDriveWrapper(){
        folderStack = new Stack<>();
        root = new File(Environment.getExternalStorageDirectory().getPath()+ "/" + "S-Cloud");
        if (!root.exists()) {
            boolean a = root.mkdir();
        }
        resetListFileTask();

    }

    public static LocalDriveWrapper getInstance(BaseApplication application){
        if (application.isDriveWrapperCreated(DriveType.DROPBOX))
            return new LocalDriveWrapper();
        return (LocalDriveWrapper) application.getDriveWrapper(DriveType.DROPBOX);
    }

    @Override
    public void getFilesInTopFolder(boolean isMore, MyCallBack caller) {
        super.getFilesInTopFolder(isMore, caller);
        if (isMore) {
            caller.callback("", DriveType.LOCAL, new ArrayList<>());
            return;
        }
        File dir = folderStack.lastElement();
        File [] files = dir.listFiles();
        caller.callback("",DriveType.LOCAL,filesToSDriveFiles(files));
    }

    @Override
    public void addNewListFileTask(String folderId) {
        folderStack.push(new File(folderId));
    }

    @Override
    public void popListFileTask() {
        folderStack.pop();
    }

    @Override
    public void resetListFileTask() {
        folderStack.removeAllElements();
        folderStack.push(root);
    }

    private ArrayList<SDriveFile> filesToSDriveFiles(File[] files){
        ArrayList<SDriveFile> sDriveFileArrayList = new ArrayList<>();
        for (File f : files){
            SDriveFile sDriveFile = f.isDirectory() ? new SDriveFolder() : new SDriveFile();
            sDriveFile.setFolder(f.getParentFile().getPath());
            sDriveFile.setId(f.getPath());
            sDriveFile.setName(f.getName());
            sDriveFile.setCloud_type(DriveType.LOCAL);
            sDriveFile.setFileSize(f.length());
            String ext = MimeTypeMap.getFileExtensionFromUrl(f.getPath());
            String mime = f.isDirectory()? "folder" : MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
            if ((ext == null || ext.length() == 0)&& (mime == null || mime.length()==0)) {
                String[] tmp = f.getName().split("[.]");
                ext = tmp.length == 0 ? "file" : tmp[tmp.length-1];
            }

            sDriveFile.setMimeType(mime);
            sDriveFile.setExtension(ext);
            sDriveFileArrayList.add(sDriveFile);
        }
        return sDriveFileArrayList;
    }

    @Override
    protected boolean createNewFolder(String name, String parent) throws DbxException, IOException {
        super.createNewFolder(name, parent);
        File file = new File(parent + "/" + name);
        return file.mkdir();
    }

    @Override
    protected boolean delete(String file) throws DbxException, IOException {
        File fileIns = new File(file);
        return fileIns.delete();

    }
}
