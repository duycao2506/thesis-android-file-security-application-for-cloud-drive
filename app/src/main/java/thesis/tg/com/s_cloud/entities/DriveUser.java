package thesis.tg.com.s_cloud.entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.from_local.MockData;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.data.from_server.GETRequestService;
import thesis.tg.com.s_cloud.framework_components.data.from_server.GeneralResponse;
import thesis.tg.com.s_cloud.framework_components.data.from_server.POSTRequestService;
import thesis.tg.com.s_cloud.framework_components.data.from_server.RequestService;
import thesis.tg.com.s_cloud.framework_components.entity.SuperObject;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.security.SimpleRSACipher;
import thesis.tg.com.s_cloud.user_interface.activity.LoginActivity;
import thesis.tg.com.s_cloud.utils.DataUtils;
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
    private String mainKey="%QW_RQWWW";

    public String getMainKey() {
        return mainKey;
    }

    public void setMainKey(String mainKey) {
        this.mainKey = mainKey;
    }

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

    public void signOut(Context context, final MyCallBack caller) {
        this.setId(null);
        Map<Integer, String> sa = new HashMap<Integer, String>();
        this.drive_ids.clear();
        this.drive_ids.put(DriveType.LOCAL,"local");
        JSONObject jso = new JSONObject();
        try {
            jso.put("mac_address", DataUtils.getMacAddress("wlan0",context));
        } catch (JSONException e) {
            e.printStackTrace();
            caller.callback("",1, false);
            return;
        }
        POSTRequestService prsc = new POSTRequestService(context, RequestService.RequestServiceConstant.logout, new MyCallBack() {
            @Override
            public void callback(String message, int code, Object data) {
                GeneralResponse gr = (GeneralResponse) data;
                try {
                    JSONObject resp = new JSONObject(gr.getResponse());
                    if (resp.getString("status").compareTo("success") == 0){
                        caller.callback("",1,true);
                    }else{
                        caller.callback("",1,false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    caller.callback("",1,false);
                }
            }
        }, new GeneralResponse());
        Map<String, Object> headers = RequestService.getBaseHeaders();
        headers.put("Authorization", "Bearer " + accesstoken);
        Log.e("ACC",accesstoken);
        prsc.setHeaders(headers);
        prsc.setJsonObject(jso);
        prsc.executeService();
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
        this.accesstoken = getAccessToken(context);
        if (isSignedIn()) {
            myCallBack.callback(EventConst.LOGIN_SCLOUD, 1, true);
        } else if (this.accesstoken == null || this.accesstoken.length() == 0) {
            myCallBack.callback(EventConst.LOGIN_SCLOUD, 1, false);
        } else {
            final JWT acctoken = new JWT(this.accesstoken);
            boolean isExpired = acctoken.getExpiresAt().compareTo(new Date()) < 0;
            if (isExpired){
                myCallBack.callback(EventConst.LOGIN_SCLOUD,1,false);
                saveAccToken(context,"");
                return;
            }
            Log.e("accesst",accesstoken);



            GETRequestService prs = new GETRequestService
                    (context, RequestService.RequestServiceConstant.profile, new MyCallBack() {
                @Override
                public void callback(String message, int code, Object data) {
                    GeneralResponse gr = (GeneralResponse) data;
                    JSONObject jso;
                    try {
                        jso = new JSONObject(gr.getResponse());
                        if (jso.getString("status").compareTo("success") != 0){
                            myCallBack.callback(EventConst.LOGIN_SCLOUD,1,false);
                            return;
                        }
                        BaseApplication ba = (BaseApplication) context.getApplicationContext();
                        if (ba.getSimpleCipher()==null) {
                            final String mac_addr = DataUtils.getMacAddress("wlan0", context);
                            if (mac_addr.length() == 0) {
                                Toast.makeText(context, R.string.plsusewifi, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            try {

                                ba.setSimpleCipher(new SimpleRSACipher(mac_addr, Build.BRAND, Build.MODEL));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                Toast.makeText(context, R.string.cannotsecupack, Toast.LENGTH_SHORT).show();
                            }
                        }


                        //TODO: get Profile
                        setMainKey(acctoken.getClaim("key").asString());
                        setId(acctoken.getClaim("sub").asString());
                        setJob(jso.getString("job"));
                        setName(jso.getString("fullname"));
                        if (!jso.isNull("birthday") && jso.getString("birthday").length() > 0){
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
                            Date d = sdf.parse(jso.getString("birthday"));
                            SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                            setBirthday(sdf2.format(d));
                        }
                        setCountry(jso.getString("country"));
                        if (jso.has("email"))
                            setEmail(jso.getString("email"));
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                        myCallBack.callback(EventConst.LOGIN_SCLOUD,1,false);
                        return;
                    }
                    myCallBack.callback(EventConst.LOGIN_SCLOUD,1,true);

                }
            }, new GeneralResponse());
            Map<String, Object> headers = RequestService.getBaseHeaders();
            headers.put("Authorization", "Bearer " + accesstoken);
            prs.setHeaders(headers);
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

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }
}
