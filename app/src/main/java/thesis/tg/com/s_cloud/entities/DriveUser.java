package thesis.tg.com.s_cloud.entities;

import android.net.Uri;

import thesis.tg.com.s_cloud.framework_components.entity.SuperObject;

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

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSignedIn(){
        return !(getId() == null);
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
}
