package thesis.tg.com.s_cloud.user_interface.activity;

import android.app.ProgressDialog;
import android.app.admin.DeviceAdminInfo;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.system.Os;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.from_local.MockData;
import thesis.tg.com.s_cloud.data.from_third_party.dropbox.DbxDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleDriveWrapper;
import thesis.tg.com.s_cloud.entities.DriveUser;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.data.from_server.GETRequestService;
import thesis.tg.com.s_cloud.framework_components.data.from_server.GeneralResponse;
import thesis.tg.com.s_cloud.framework_components.data.from_server.POSTRequestService;
import thesis.tg.com.s_cloud.framework_components.data.from_server.RequestService;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;
import thesis.tg.com.s_cloud.utils.DataUtils;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.UiUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static thesis.tg.com.s_cloud.utils.EventConst.LOGIN_CANCEL_RESULT_CODE;
import static thesis.tg.com.s_cloud.utils.EventConst.LOGIN_SUCCESS;
import static thesis.tg.com.s_cloud.utils.EventConst.RESOLVE_CONNECTION_REQUEST_CODE;

public class LoginActivity extends AppCompatActivity implements  View.OnClickListener, MyCallBack{

    Button btnLogin, btnDropbox, btnGoogle;
    EditText edtusername, edtpassword;
    TextView btnSignup;
    private GoogleApiClient mGoogleApiClient;
//
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
            this.callback(LOGIN_SUCCESS,DriveType.DROPBOX,null);
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
                Intent ins = new Intent(this, RegisterActivity.class);
                startActivityForResult(ins, EventConst.SIGN_UP_REQUEST_CODE);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    progressDialog.show();
                    if (result.isSuccess())
                        this.callback(LOGIN_SUCCESS, DriveType.GOOGLE,null);
                }
                break;
            case EventConst.SIGN_UP_REQUEST_CODE:
                if (resultCode == RESULT_OK){
                    JSONObject auth_json;
                    JSONObject user_json;
                    try {
                        auth_json = new JSONObject(data.getStringExtra(EventConst.AUTH_JSON_STRING));
                        user_json = new JSONObject(data.getStringExtra(EventConst.USER_JSON));
                        Log.d("userjson",user_json.toString());
                        Log.d("authjson",auth_json.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
        }
    }


    @Override
    public void onBackPressed() {
        UiUtils.showExitAlertDialog(this,LOGIN_CANCEL_RESULT_CODE);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
