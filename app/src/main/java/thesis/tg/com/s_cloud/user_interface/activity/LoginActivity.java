package thesis.tg.com.s_cloud.user_interface.activity;

import android.app.ProgressDialog;
import android.app.admin.DeviceAdminInfo;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.system.Os;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.from_local.MockData;
import thesis.tg.com.s_cloud.data.from_third_party.dropbox.DbxDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleDriveWrapper;
import thesis.tg.com.s_cloud.entities.DriveUser;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.data.from_server.GETRequestService;
import thesis.tg.com.s_cloud.framework_components.data.from_server.GeneralResponse;
import thesis.tg.com.s_cloud.framework_components.data.from_server.POSTRequestService;
import thesis.tg.com.s_cloud.framework_components.data.from_server.RequestService;
import thesis.tg.com.s_cloud.user_interface.fragment.FileInfoFragment;
import thesis.tg.com.s_cloud.user_interface.fragment.PasswordSetFragment;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;
import thesis.tg.com.s_cloud.utils.DataUtils;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.ResourcesUtils;
import thesis.tg.com.s_cloud.utils.UiUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static thesis.tg.com.s_cloud.utils.EventConst.LOGIN_CANCEL_RESULT_CODE;
import static thesis.tg.com.s_cloud.utils.EventConst.LOGIN_SUCCESS;
import static thesis.tg.com.s_cloud.utils.EventConst.RESOLVE_CONNECTION_REQUEST_CODE;

public class LoginActivity extends AppCompatActivity implements  View.OnClickListener, MyCallBack{

    Button btnLogin, btnDropbox, btnGoogle;

    boolean signUpDbx = false;

    String jsonObject =
            "{" +
                    "\"sda\":\"dsa\"";

    EditText edtusername, edtpassword;
    TextView btnSignup;
    private GoogleApiClient mGoogleApiClient;
//

    JSONObject registerObject;
    GoogleDriveWrapper gdwrapper;
    BaseApplication ba;


    GoogleAccountCredential mCredential;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Call Drive API";
    private static final String PREF_ACCOUNT_NAME = "accountName";

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ba = (BaseApplication) getApplication();

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Drive API ...");
        // Initialize credentials and service object.

        gdwrapper = (GoogleDriveWrapper) ba.getDriveWrapper(DriveType.GOOGLE);
        this.mGoogleApiClient = gdwrapper.getClient();
        if (this.mGoogleApiClient == null ){
            this.mGoogleApiClient = gdwrapper.googleSignInServiceInit(ba);
        }

        // btn sign up
        btnSignup = (TextView) findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(this);




        controlInit();


        animate();





    }

    @Override
    protected void onResume() {
        super.onResume();
        String token = com.dropbox.core.android.Auth.getOAuth2Token();
        if (token != null){
            ((DbxDriveWrapper)ba.getDriveWrapper(DriveType.DROPBOX)).saveAccToken(this, token);
            if (signUpDbx){
                signUpDbx = false;
                getInfoDropbox(this, token);
            }
        }
    }


    private void animate() {
        ImageView ivCloud1 = (ImageView) findViewById(R.id.cloud1);
        ImageView ivCloud2 = (ImageView) findViewById(R.id.cloud2);
        ImageView ivCloud3 = (ImageView) findViewById(R.id.cloud3);

        ImageView ivCloud4 = (ImageView) findViewById(R.id.cloud4);

        ImageView ivCloud5 = (ImageView) findViewById(R.id.cloud5);

        ImageView ivCloud6 = (ImageView) findViewById(R.id.cloud6);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.right_left_trans);
        anim.setStartTime(500);
        Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.right_left_trans);
        anim2.setStartTime(1000);
        Animation anim3 = AnimationUtils.loadAnimation(this, R.anim.right_left_trans);
        anim3.setDuration(2000);
        Animation anim4 = AnimationUtils.loadAnimation(this, R.anim.right_left_trans);
        anim4.setDuration(5000);
        Animation anim5 = AnimationUtils.loadAnimation(this, R.anim.right_left_trans);
        anim5.setDuration(2500);
        Animation anim6 = AnimationUtils.loadAnimation(this, R.anim.right_left_trans);
        anim6.setDuration(6000);

        ivCloud1.startAnimation(anim);
        ivCloud2.startAnimation(anim2);
        ivCloud3.startAnimation(anim3);
        ivCloud6.startAnimation(anim4);
        ivCloud5.startAnimation(anim5);
        ivCloud6.startAnimation(anim6);

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                //DataUtils.waitFor(200,LoginActivity.this,null);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                UiUtils.OpeningAnimate(edtusername,Techniques.FadeInLeft, UiUtils.TIME_ANIMATE_STANDARD);
                UiUtils.OpeningAnimate(edtpassword,Techniques.FadeInRight, UiUtils.TIME_ANIMATE_STANDARD);
                UiUtils.OpeningAnimate(btnLogin,Techniques.FadeIn, UiUtils.TIME_ANIMATE_STANDARD);
//                UiUtils.OpeningAnimate(btnDropbox,Techniques.FadeIn, UiUtils.TIME_ANIMATE_STANDARD);
//                UiUtils.OpeningAnimate(btnGoogle,Techniques.FadeIn, UiUtils.TIME_ANIMATE_STANDARD);


            }
        }.execute();




    }

    private void controlInit() {
        this.btnLogin = (Button) findViewById(R.id.btnlogin);
        this.btnLogin.setActivated(true);
        btnLogin.setOnClickListener(this);
//        this.btnGoogle = (Button) findViewById(R.id.btngoogle);
//        btnGoogle.setOnClickListener(this);
//        this.btnDropbox = (Button) findViewById(R.id.btnfacebook);
//        btnDropbox.setOnClickListener(this);
        this.edtusername = (EditText) findViewById(R.id.edtUsr);
        this.edtpassword = (EditText) findViewById(R.id.edtPassword);
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnlogin:
                progressDialog = UiUtils.getDefaultProgressDialog(this,false,getString(R.string.signin_ing));
                progressDialog.show();


                //GETACCESSTOKEN
                POSTRequestService prs = new POSTRequestService(this, RequestService.RequestServiceConstant.api1, new MyCallBack() {
                    @Override
                    public void callback(String message, int code, Object data) {
                        Log.d("RESP", ((GeneralResponse)data).getResponse());
                        String auth_json_string = MockData.auth_resp;
                        JSONObject auth_json;
                        try{
                            auth_json = new JSONObject(auth_json_string);
                            if (auth_json.getString("status").compareTo("success")==0)
                                ba.getDriveUser().saveAccToken(LoginActivity.this,auth_json.getString("auth_token"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //GETPROFILE
                        POSTRequestService prs2 = new POSTRequestService(LoginActivity.this, RequestService.RequestServiceConstant.api1, new MyCallBack() {
                            @Override
                            public void callback(String message, int code, Object data) {
                                Log.d("RESP", ((GeneralResponse)data).getResponse());
                                //TODO: generate response json and check success status
                                try{
                                    JSONObject jso = new JSONObject(((GeneralResponse) data).getResponse());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ba.getDriveUser().copyFromJSON(MockData.jsonuser);
                                LoginActivity.this.callback(EventConst.LOGIN_SUCCESS,1,null);
                            }
                        }, new GeneralResponse());
                        prs2.executeService();

                    }
                }, new GeneralResponse());
                prs.executeService();
                break;
//            case R.id.btngoogle:
//                if (mGoogleApiClient.isConnected())
//                    gdwrapper.signOut(null);
//                else {
//                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//                    startActivityForResult(signInIntent, RESOLVE_CONNECTION_REQUEST_CODE);
//                }
//                break;
//            case R.id.btnfacebook:
//                com.dropbox.core.android.Auth.startOAuth2Authentication(this,getString(R.string.dbox_app_key));
//                break;
            case R.id.btnSignup:
                initRegisterObj();
                buildSignUpOptionMenu();
//                Intent ins = new Intent(this, RegisterActivity.class);
//                startActivityForResult(ins, EventConst.SIGN_UP_REQUEST_CODE);
                break;
            default:
                break;
        }
    }

    private void initRegisterObj() {
        this.registerObject = new JSONObject();
        try {
            this.registerObject.put("email", "Not available");
            this.registerObject.put("password", "Not available");
            this.registerObject.put("birthday", "");
            this.registerObject.put("job", "Not available");
            this.registerObject.put("country", "Not available");
            this.registerObject.put("fullname", "Not available");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    if (result.isSuccess())
                        getInfoGoogle(this,result);
                }
                break;
            case EventConst.SIGN_UP_REQUEST_CODE:
                if (resultCode == RESULT_OK){
                    JSONObject user_json;
                    try {
                        this.registerObject = new JSONObject(data.getStringExtra(EventConst.USER_JSON));
                        Log.d("userjson",this.registerObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    this.callback(EventConst.SIGN_UP_SENT,DriveType.LOCAL,this.registerObject);
                }
                break;
            default:
                break;
        }

    }


    private void finishLogin(){
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void callback(String message, int code, Object data) {
        switch (message){
            case EventConst.LOGIN_SUCCESS:
                progressDialog.dismiss();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                break;
            case EventConst.LOGIN_FAIL:
                Toast.makeText(this, R.string.fail_login,Toast.LENGTH_LONG).show();
                break;
            case EventConst.DBX_CONNECT:
            case EventConst.GOOGLE_CONNECT:
                PasswordSetFragment df = new PasswordSetFragment();
                df.setCaller(this);
                df.show(this.getSupportFragmentManager(),this.getString(R.string.file_info));
                break;
            case EventConst.CANCEL_SIGNUP_DROPBOX:
                ((DbxDriveWrapper)ba.getDriveWrapper(DriveType.DROPBOX)).saveAccToken(this, null);
                initRegisterObj();
                break;
            case EventConst.CANCEL_SIGNUP_GOOGLE:
                progressDialog = UiUtils.getDefaultProgressDialog(this,false,getString(R.string.canceling));
                progressDialog.show();
                ((GoogleDriveWrapper) ba.getDriveWrapper(DriveType.GOOGLE)).signOut(new MyCallBack() {
                    @Override
                    public void callback(String message, int code, Object data) {
                        if ((boolean)data){
                            Log.d("CANCEL_SIGUP_GG","SUCC");
                        }else
                            Log.d("CANCEL_SIGUP_GG","FAIL");
                        initRegisterObj();
                        progressDialog.dismiss();
                    }
                });
                break;
            case EventConst.SET_PASSWORD:
                try {
                    registerObject.put("password",DataUtils.encodePassword(
                            registerObject.get("email").toString(),
                            data.toString()));
                    registerObject.put("birthday","22/2/1900");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            case EventConst.SIGN_UP_SENT:
                Log.d("REGOBJ",registerObject.toString());
                final ProgressDialog pd = UiUtils.getDefaultProgressDialog(this,false,getString(R.string.signing_up));
                pd.setCancelable(false);
                pd.show();
                POSTRequestService prs = new POSTRequestService(this, RequestService.RequestServiceConstant.register, new MyCallBack() {
                    @Override
                    public void callback(String message, int code, Object data) {
                        GeneralResponse gr = (GeneralResponse) data;
                        Log.d("RESP",gr.getResponse());
                        pd.dismiss();
                        try {
                            JSONObject resp = new JSONObject(gr.getResponse());
                            if (resp.get("status").toString().compareTo("success")==0)
                                Toast.makeText(LoginActivity.this,getString(R.string.reg_succ_confirm),Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },new GeneralResponse());
                prs.setJsonObject(registerObject);
                prs.executeService();
                break;
        }
    }



    public void buildSignUpOptionMenu() {
        final BaseApplication ba = (BaseApplication) this.getApplicationContext();
        final ResourcesUtils resrcMnger = ba.getResourcesUtils();
        BottomSheetBuilder bsb = new BottomSheetBuilder(this, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .addTitleItem(R.string.register_dot)
                .setTitleTextColorResource(R.color.gray_dark_transparent)
                .setItemClickListener(new BottomSheetItemClickListener() {
                    @Override
                    public void onBottomSheetItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case DriveType.DROPBOX:
                                signUpDbx=true;
                                com.dropbox.core.android.Auth.startOAuth2Authentication(LoginActivity.this, getString(R.string.dbox_app_key));
                                break;
                            case DriveType.GOOGLE:
                                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                                startActivityForResult(signInIntent, RESOLVE_CONNECTION_REQUEST_CODE);
                                break;
                            case DriveType.LOCAL:
                                Intent ins = new Intent(LoginActivity.this, RegisterActivity.class);
                                ins.putExtra("registerObj",registerObject.toString());
                                startActivityForResult(ins, EventConst.SIGN_UP_REQUEST_CODE);
                                break;
                        }
                    }
                });

        for (int i = 0; i < ba.getDriveMannager().getNumDrive(); i++){
            int type = resrcMnger.getTypeByIndex(i);
            String optiontitle = "with " + ((type != DriveType.LOCAL) ? getString(resrcMnger.getStringId(type)) : "a form");
            bsb = bsb.addItem(type,optiontitle, resrcMnger.getDriveIconId(type));
        }


        BottomSheetMenuDialog dialog = bsb.createDialog();
//        dialog.setTitle(this.getString(R.string.which_drive));
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        UiUtils.showExitAlertDialog(this,LOGIN_CANCEL_RESULT_CODE);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }



    // Drive Sign Up Implementation
    public void getInfoDropbox(final MyCallBack caller, final String accessToken){
        progressDialog = UiUtils.getDefaultProgressDialog(this,false,getString(R.string.getdbxinfo));
        progressDialog.show();
        new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void... params) {
                DbxRequestConfig dbxRequestConfig = DbxRequestConfig.newBuilder("scloud/dgproduction").build();
                DbxClientV2 dbxClientV2 = new DbxClientV2(dbxRequestConfig,accessToken);

                //GEt ACcount;
                FullAccount fa = null;
                try {
                    fa = dbxClientV2.users().getCurrentAccount();
                } catch (DbxException e) {
                    e.printStackTrace();
                }
                if (fa != null){
                    //TODO: send accesstoken to get info from server
                    try {
                        registerObject.put("email",fa.getEmail());
                        registerObject.put("country",fa.getCountry());
                        registerObject.put("fullname",fa.getName());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return EventConst.DBX_CONNECT;
                }
                return EventConst.LOGIN_FAIL;
            }

            @Override
            protected void onPostExecute(String s) {
                caller.callback(s, DriveType.DROPBOX, null);
                progressDialog.dismiss();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void getInfoGoogle(final MyCallBack caller, final GoogleSignInResult result){
        progressDialog = UiUtils.getDefaultProgressDialog(this,false,getString(R.string.getgoogleinfo));
        new AsyncTask<Void, Void, String>() {
            BaseApplication ba = (BaseApplication) LoginActivity.this.getApplicationContext();
            @Override
            protected String doInBackground(Void... params) {
                if (result.isSuccess()) {
                    mGoogleApiClient.connect();
//                    if (1 == 1)
//                        return LOGIN_FAIL;
                    // Signed in successfully, show authenticated UI.
                    GoogleSignInAccount acct = result.getSignInAccount();

                    //TODO: Create User
                    try {
                        registerObject.put("email",acct.getEmail());
                        registerObject.put("fullname",acct.getDisplayName());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //wait until connection is established
                    return EventConst.GOOGLE_CONNECT;
                } else {
                    return EventConst.LOGIN_FAIL;
                }
            }

            @Override
            protected void onPostExecute(String aVoid) {
                super.onPostExecute(aVoid);
                progressDialog.dismiss();
                caller.callback(aVoid, DriveType.GOOGLE, null);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
            }
        }.execute();
    }
}
