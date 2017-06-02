package thesis.tg.com.s_cloud.entities;

import android.net.Uri;

import thesis.tg.com.s_cloud.framework_components.entity.SuperObject;
import thesis.tg.com.s_cloud.utils.DriveType;

/**
 * Created by admin on 5/6/17.
 */

public class DriveUser extends SuperObject {
    private String email;
    private String dropbox_id;
    private String google_id;
    private Uri avatar;
    private String avatarLink;
    private String country;
    private String phone;
    private String address;
    private String google_email;
    private String dbx_email;


    public String getDropbox_id() {
        return dropbox_id;
    }

    public void setDropbox_id(String dropbox_id) {
        this.dropbox_id = dropbox_id;
    }

    public String getGoogle_id() {
        return google_id;
    }

    public void setGoogle_id(String google_id) {
        this.google_id = google_id;
    }

    protected DriveUser(){
    }

    private static DriveUser instance;


    public static DriveUser getInstance(){
        if (instance == null)
            instance = new DriveUser();
        return instance;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public String getCountry() {
        return country;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getDbx_email() {
        return dbx_email;
    }

    public String getGoogle_email() {
        return google_email;
    }

    public void setGoogleEmail(String email) {
        this.google_email = email;
    }

    public void setDropboxEmail(String email){
        this.dbx_email = email;
    }

    public boolean isSignedIn(int type){
        switch (type){
            case DriveType.GOOGLE:
                return google_id != null && google_id.length() > 0;
            case DriveType.DROPBOX:
                return dropbox_id != null && dropbox_id.length() > 0;
            default:
                return true;
        }
    }

    public boolean isSignedIn(){
        return (getId() != null && getId().length() > 0) ||
                (google_id != null && google_id.length() > 0) ||
                (dropbox_id != null && dropbox_id.length() > 0);
    }

    public String getAvatar() {
        return avatar == null ? avatarLink : avatar.toString();
    }

    public void setAvatar(Uri avatar) {
        this.avatar = avatar;
    }


    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    public int getAvailableDrive(){
        if (this.google_id != null && this.google_id.length() > 0  ) return DriveType.GOOGLE;
        if (this.dropbox_id != null && this.dropbox_id.length() > 0) return DriveType.DROPBOX;
        return -1;
    }
}
