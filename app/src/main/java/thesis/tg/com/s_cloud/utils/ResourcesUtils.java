package thesis.tg.com.s_cloud.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseArray;

import java.util.HashMap;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.DrivesManager;
import thesis.tg.com.s_cloud.entities.SDriveFile;

/**
 * Created by CKLD on 5/27/17.
 */

public class ResourcesUtils {

    private int[] strings = {R.string.g_drive, R.string.dbox, R.string.local_storage};
    private int[] icons = {R.drawable.ic_gdrive, R.drawable.ic_dbox, R.drawable.ic_localstorage };
    private int[] types = {DriveType.GOOGLE, DriveType.DROPBOX, DriveType.LOCAL};


    private SparseArray hashmapOrderResources;
    private HashMap<String, Integer> hashmapFileType;

    private static ResourcesUtils instance;

    public static ResourcesUtils getInstance(){
        if (instance == null)
            instance = new ResourcesUtils();
        return instance;
    }

    private ResourcesUtils(){
        //drive type resources
        hashmapOrderResources = new SparseArray();
        for (int i = 0; i < DrivesManager.getInstance().getNumDrive(); i ++)
            hashmapOrderResources.put(types[i],i);

        //file type resources
        hashmapFileType = new HashMap<>();
        String[][] filetypes = {
                //Excel
                {"spreadsheet", "xls", "csv"},
                //Word
                {"doc", "docx", "form", "presentation"},
                //Archive
                {"zip", "rar", "7z"},
                //PDF
                {"pdf"},
                //Folder
                {"folder"},
                //Image
                {"png", "jpg", "svg", "jpeg", "gif"},
                //Sound
                {"octet-stream", "mp3", "wav", "wma", "ogg"},
                //Video,
                {"mp4", "avi", "mkv", "flv"},
                //code,
                {"x-javascript", "html", "htm", "js", "cpp","java"},
                //exe
                {"exe"},
                //apk
                {"apk"}
        };
        int[] resources = {
                R.drawable.ic_excel,
                R.drawable.ic_word,
                R.drawable.ic_archive,
                R.drawable.ic_pdf,
                R.drawable.ic_folder,
                R.drawable.ic_image,
                R.drawable.ic_audio,
                R.drawable.ic_video,
                R.drawable.ic_code,
                R.drawable.ic_exe,
                R.drawable.ic_apk
        };
        for (int i = 0; i < resources.length; i++){
            for (int j =0 ;j < filetypes[i].length; j++)
                hashmapFileType.put(filetypes[i][j],resources[i]);
        }
    }

    public int parseFileTypeColor(Context context, boolean isFolder){
        return Color.parseColor(context.getString(R.string.gray_transparent));
    }

    public int getFileIconRes(SDriveFile sdf){
        String ext = sdf.getExtension();
        if (ext==null || ext.length() == 0){
            ext = sdf.getMimeType();
            String[] tokens = ext.split("[/\\.]");
            ext = tokens[tokens.length-1];
        }
        Integer result = hashmapFileType.get(ext);
        if (result == null) return R.drawable.ic_file;
        return result;
    }

    public int getDriveIconId(int type){
        return icons[(int) hashmapOrderResources.get(type)];
    }

    public int getStringId(int type){
        return strings[(int) hashmapOrderResources.get(type)];
    }

    public int getTypeByIndex(int index){
        return types[index];
    }

    public int getIndexByType(int driveType){
        return (int) hashmapOrderResources.get(driveType);
    }


}