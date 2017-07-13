package thesis.tg.com.s_cloud.entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.from_local.MockData;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.data.from_server.GeneralResponse;
import thesis.tg.com.s_cloud.framework_components.data.from_server.POSTRequestService;
import thesis.tg.com.s_cloud.framework_components.data.from_server.RequestService;
import thesis.tg.com.s_cloud.framework_components.entity.SuperObject;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by admin on 5/6/17.
 */

public class DriveUser extends SuperObject {
    private String email;
    private Map<Integer, String> drive_ids;
    private Uri avatar;
    private String avatarLink;
    private String country;
    private String phone;
    private String job;
    private String birthday;
    private String address;
    private String google_email;
    private String dbx_email;
    private int defaultAvatar;
    private String accesstoken;


    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getId(int type) {
        return this.drive_ids.get(type);
    }

    public void setId(int type, String id) {
        this.drive_ids.put(type, id);
    }

    protected DriveUser() {
        this.defaultAvatar = R.drawable.avatar;
        this.drive_ids = new HashMap<Integer, String>();
        this.drive_ids.put(DriveType.LOCAL, "local");
        this.job = "";
        this.email = "";
        this.setName("");
        this.country = "";
        this.setBirthday("");
    }

    private static DriveUser instance;


    public static DriveUser getInstance(BaseApplication ba) {
        if (ba.getDriveUser() != null)
            return ba.getDriveUser();
        ba.setDriveUser(new DriveUser());
        return ba.getDriveUser();
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

    public void setDropboxEmail(String email) {
        this.dbx_email = email;
    }

    public boolean isSignedIn(int type) {
        return (this.drive_ids.get(type) != null);
    }

    public boolean isSignedIn() {
        return (getId() != null && getId().length() > 0);
    }

    public Uri getAvatar() {
        return avatar;
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


    public ArrayList<Integer> getAvailableDrives() {
        Set<Integer> drivekeys = drive_ids.keySet();
        ArrayList<Integer> availKeys = new ArrayList<>();
        for (Integer key: drivekeys
             ) {
            if (key == DriveType.LOCAL) continue;
            if (this.drive_ids.get(key) != null) availKeys.add(key);
        }
        return availKeys;
    }

    public void signOut() {
        this.setId(null);
        Map<Integer, String> sa = new HashMap<>();
        this.drive_ids.clear();
    }

    public void copyFromJSON(String response) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            this.setId(jsonObject.getString("id"));
            this.setName(jsonObject.getString("fullname"));
            this.setCountry(jsonObject.getString("country"));
            this.setBirthday(jsonObject.getString("birthday"));
            this.setJob(jsonObject.getString("job"));
            this.setEmail(jsonObject.getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setEmail(String email) {
        this.email = email;
    }


    private String getAccessToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getString("scloud-access-token", null);
    }

    public void saveAccToken(Context context, String token) {
        this.accesstoken = token;
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        prefs.edit().putString("scloud-access-token", token).apply();
    }

    public void autoSignIn(final Context context, final MyCallBack myCallBack) {
        if (isSignedIn()) {
            myCallBack.callback(EventConst.LOGIN_SCLOUD, 1, true);
        } else if (getAccessToken(context) == null) {
            myCallBack.callback(EventConst.LOGIN_SCLOUD, 1, false);
        } else {
            POSTRequestService prs = new POSTRequestService
                    (context, RequestService.RequestServiceConstant.api1, new MyCallBack() {
                @Override
                public void callback(String message, int code, Object data) {
                    //TODO: check validation

                    //TODO: get Profile by AccessToken
                    Log.d("AutoLogin", data.toString());
                    //Processing Profile
                    ((BaseApplication)context.getApplicationContext())
                            .getDriveUser().copyFromJSON(MockData.jsonuser);
                    myCallBack.callback(EventConst.LOGIN_SCLOUD, 1, true);
                }
            }, new GeneralResponse());
            prs.executeService();
        }
    }

    public int getDefaultAvatar() {
        return defaultAvatar;
    }

    public void deleteAccessToken(Context context) {
        this.saveAccToken(context, null);
    }

    public String getAccesstoken() {
        return accesstoken;
    }
}
