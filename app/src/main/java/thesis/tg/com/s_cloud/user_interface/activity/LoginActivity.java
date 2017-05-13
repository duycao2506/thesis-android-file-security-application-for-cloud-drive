package thesis.tg.com.s_cloud.user_interface.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.from_third_party.GoogleDriveWrapper;
import thesis.tg.com.s_cloud.utils.EventConst;
import thesis.tg.com.s_cloud.utils.DataUtils;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.UiUtils;

import static thesis.tg.com.s_cloud.utils.EventConst.LOGIN_CANCEL_RESULT_CODE;
import static thesis.tg.com.s_cloud.utils.EventConst.LOGIN_SUCCESS_RESULT_CODE;
import static thesis.tg.com.s_cloud.utils.EventConst.RESOLVE_CONNECTION_REQUEST_CODE;

public class LoginActivity extends AppCompatActivity implements  View.OnClickListener, MyCallBack{

    Button btnLogin, btnFacebook, btnGoogle;
    private GoogleApiClient mGoogleApiClient;
//
    GoogleDriveWrapper gdwrapper;


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


        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Drive API ...");
        // Initialize credentials and service object.

        gdwrapper = GoogleDriveWrapper.getInstance();
        this.mGoogleApiClient = gdwrapper.getClient();



        controlInit();


        animate();





    }

    @Override
    protected void onStart() {
        super.onStart();
//        mGoogleApiClient.connect();
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
    }

    private void controlInit() {
        this.btnLogin = (Button) findViewById(R.id.btnlogin);
        btnLogin.setOnClickListener(this);
        this.btnGoogle = (Button) findViewById(R.id.btngoogle);
        btnGoogle.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnlogin:

                progressDialog.show();
                DataUtils.waitFor(3000, LoginActivity.this, new MyCallBack() {
                    @Override
                    public void callback(String message, int code, Object data) {
                        finish();
                    }
                });
                break;
            case R.id.btngoogle:
                if (mGoogleApiClient.isConnected())
                    gdwrapper.signOut();
                else {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RESOLVE_CONNECTION_REQUEST_CODE);
                }
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
                    gdwrapper.handleSignInResult(result, this);
                }
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
                setResult(LOGIN_SUCCESS_RESULT_CODE);
                finish();
                progressDialog.dismiss();
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
}
