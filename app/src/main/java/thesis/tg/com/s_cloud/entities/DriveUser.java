package thesis.tg.com.s_cloud.entities;

import android.net.Uri;
import android.util.SparseArray;

import thesis.tg.com.s_cloud.framework_components.entity.SuperObject;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by admin on 5/6/17.
 */

public class DriveUser extends SuperObject {
    private String email;
    private SparseArray<String> drive_ids;
    private Uri avatar;
    private String avatarLink;
    private String country;
    private String phone;
    private String address;
    private String google_email;
    private String dbx_email;


    public String getId(int type) {
        return this.drive_ids.get(type);
    }

    public void setId(int type, String id) {
        this.drive_ids.put(type,id);
    }

    protected DriveUser(){
        this.drive_ids = new SparseArray<>();
        this.drive_ids.put(DriveType.LOCAL, "local");
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
        boolean res = (this.drive_ids.get(type) != null);
        return res;
    }

    public boolean isSignedIn(){
        return (getId() != null && getId().length() > 0) ||
                (this.drive_ids.get(DriveType.GOOGLE) != null ) ||
                (this.drive_ids.get(DriveType.DROPBOX) != null);
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
        if (this.drive_ids.get(DriveType.GOOGLE) != null  ) return DriveType.GOOGLE;
        if (this.drive_ids.get(DriveType.DROPBOX) != null) return DriveType.DROPBOX;
        return -1;
    }
}
