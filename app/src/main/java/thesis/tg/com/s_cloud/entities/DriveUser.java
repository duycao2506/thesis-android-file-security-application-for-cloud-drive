package thesis.tg.com.s_cloud.entities;

import thesis.tg.com.s_cloud.framework_components.entity.SuperObject;

/**
 * Created by admin on 5/6/17.
 */

public class DriveUser extends SuperObject {
    private String email;

    private DriveUser(){

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
        return getId() == null;
    }
}