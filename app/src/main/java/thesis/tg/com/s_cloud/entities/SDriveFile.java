package thesis.tg.com.s_cloud.entities;

import thesis.tg.com.s_cloud.framework_components.entity.SuperObject;

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

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getCloud_status() {
        return cloud_type;
    }

    public void turnCloudStatus(int dt) {
        this.cloud_type ^= (1 << dt);
    }

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
}


