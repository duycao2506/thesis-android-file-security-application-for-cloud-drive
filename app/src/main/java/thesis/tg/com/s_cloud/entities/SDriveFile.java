package thesis.tg.com.s_cloud.entities;

import com.dropbox.core.DbxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import thesis.tg.com.s_cloud.data.DrivesManager;
import thesis.tg.com.s_cloud.data.from_third_party.dropbox.DbxDriveWrapper;
import thesis.tg.com.s_cloud.framework_components.entity.SuperObject;
import thesis.tg.com.s_cloud.utils.DriveType;

/**
 * Created by admin on 4/26/17.
 */


public class SDriveFile extends SuperObject {


    /**
     * Using binary to express
     */
    //bit 0 : Google Drive, bit 1: Dropbox
    int cloud_type = 0;
    String mimeType;
    String createdDate;
    String link;
    long fileSize;
    String thumbnail;
    private String iconLink;
    private String extension;
    private String folder;

    public SDriveFile() {
    }

    public SDriveFile(File file, String mimeType) {
        this.setName(file.getName());
        this.setMimeType(mimeType);
        this.setFileSize(file.length());
        this.setLink(file.getPath());
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }


    /**
     * for single drive file
     * @return
     */
    public int getCloud_type() {
        return cloud_type;
    }

    public void setCloud_type(int cloud_type) {
        this.cloud_type = cloud_type;
    }

    ///


    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setIconLink(String iconLink) {
        this.iconLink = iconLink;
    }

    public String getIconLink() {
        return iconLink;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public InputStream getInputstream(){
        switch (cloud_type){
            case DriveType.DROPBOX:
                InputStream is = null;
                try {
                    is =  DbxDriveWrapper.getInstance().getClient().files().download(getId()).getInputStream();
                } catch (DbxException e) {
                    e.printStackTrace();
                }
                return is;
            case DriveType.LOCAL:
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(getLink());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return fis;
            default:
                return null;
        }
    }

    public OutputStream getOutputStream(int driveType, String toPath){
        switch (driveType){
            case DriveType.DROPBOX:
                OutputStream os = null;
                try {
                    os = DbxDriveWrapper.getInstance().getClient().files().upload(toPath).getOutputStream();
                } catch (DbxException e) {
                    e.printStackTrace();
                }
                return os;
            case DriveType.LOCAL:
                FileOutputStream fos = null;
                String localfolder = DrivesManager.getInstance().getLocalPath();
                File desFile = new File(localfolder, toPath);
                try {
                    boolean a = desFile.createNewFile();
                    fos = new FileOutputStream(desFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return fos;
            default:
                return null;
        }
    }


    /**
     * Get Parent Folder Representative String
     * @return
     */
    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}


