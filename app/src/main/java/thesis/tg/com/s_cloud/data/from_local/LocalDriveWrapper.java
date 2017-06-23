package thesis.tg.com.s_cloud.data.from_local;

import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.dropbox.core.DbxException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import thesis.tg.com.s_cloud.data.CloudDriveWrapper;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.entities.SDriveFolder;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by admin on 5/20/17.
 */

public class LocalDriveWrapper extends CloudDriveWrapper {
    Stack<File> folderStack;
    File root;

    String searchString = null;
    private LocalDriveWrapper(){
        folderStack = new Stack<>();
        root = new File(Environment.getExternalStorageDirectory().getPath(),"S-Cloud");
        if (!root.exists()) {
            boolean a = root.mkdirs();
            if (a)
                Log.d("DSAD","DSADSAD");
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
        if (folderStack.lastElement().getPath().compareTo(EventConst.SEARCH) != 0) {
            File dir = folderStack.lastElement();
            File[] files = dir.listFiles();
            caller.callback("", DriveType.LOCAL, filesToSDriveFiles(files));
        }else{
            List<File> filesraw = getListFilesSearch(this.root);
            File[] files = new File[filesraw.size()];
            caller.callback("",DriveType.LOCAL,filesToSDriveFiles(getListFilesSearch(this.root).toArray(files)));
        }
    }

    @Override
    public void addNewListFileTask(String folderId) {
        if (folderId == EventConst.SEARCH)
            searchString = "";
        folderStack.push(new File(folderId));
    }

    @Override
    public void popListFileTask() {
        if (folderStack.size() == 2 && searchString != null){
            searchString = null;
        }
        folderStack.pop();
    }

    @Override
    public void resetListFileTask() {
        folderStack.removeAllElements();
        folderStack.push(root);
    }

    private ArrayList<SDriveFile> filesToSDriveFiles(File[] files){
        ArrayList<SDriveFile> sDriveFileArrayList = new ArrayList<>();
        if (files == null) return new ArrayList<SDriveFile>();
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
            if (f.isDirectory())
                sDriveFileArrayList.add(0,sDriveFile);
            else
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

    @Override
    public void updateSearchContext(String query) {
//        super.updateSearchContext(query);
        this.searchString = query;
    }



    private List<File> getListFilesSearch(File parentDir) {
        List<File> inFiles = new ArrayList<>();
        Queue<File> files = new LinkedList<>();
        files.addAll(Arrays.asList(parentDir.listFiles()));
        while (!files.isEmpty()) {
            File file = files.remove();
            if (file.isDirectory()) {
                files.addAll(Arrays.asList(file.listFiles()));
            }
            if (file.getName().toLowerCase().contains(searchString.toLowerCase())) {
                inFiles.add(file);
            }
        }
        return inFiles;
    }
}
