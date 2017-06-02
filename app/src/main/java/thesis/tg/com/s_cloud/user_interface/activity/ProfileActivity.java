package thesis.tg.com.s_cloud.user_interface.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.CloudDriveWrapper;
import thesis.tg.com.s_cloud.data.DrivesManager;
import thesis.tg.com.s_cloud.data.from_third_party.dropbox.DbxDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleDriveWrapper;
import thesis.tg.com.s_cloud.entities.DriveUser;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DataUtils;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static thesis.tg.com.s_cloud.utils.EventConst.LOGIN_SUCCESS;
import static thesis.tg.com.s_cloud.utils.EventConst.RESOLVE_CONNECTION_REQUEST_CODE;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    CircleImageView civProfileAvatar;
    TextView tvNameHeader;
    Button btnGoogle, btnDropbox;
    GoogleApiClient gac;
    GoogleDriveWrapper gdw;
    DbxDriveWrapper ddw;
    private boolean edited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        gdw = (GoogleDriveWrapper) CloudDriveWrapper.getInstance(DriveType.GOOGLE);
        gac = gdw.getClient();
        ddw = (DbxDriveWrapper) CloudDriveWrapper.getInstance(DriveType.DROPBOX);
        setUpView();
    }

    private void setUpView() {
        civProfileAvatar = (CircleImageView) findViewById(R.id.ivProfileAvatar);
        Picasso.with(this).load(DriveUser.getInstance().getAvatar()).into(civProfileAvatar);
        tvNameHeader = (TextView) findViewById(R.id.tvHeaderName);
        tvNameHeader.setText(DriveUser.getInstance().getName());
        btnGoogle = (Button) findViewById(R.id.btnConnectGoogle);
        btnGoogle.setActivated(!DriveUser.getInstance().isSignedIn(DriveType.GOOGLE));
        btnGoogle.setOnClickListener(this);
        btnDropbox = (Button) findViewById(R.id.btnConnectDropbox);
        btnDropbox.setActivated(!DriveUser.getInstance().isSignedIn(DriveType.DROPBOX));
        btnDropbox.setOnClickListener(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnConnectGoogle:
                edited = true;
                if (edited) {
                    setResult(EventConst.LOGIN_SUCCESS_RESULT_CODE);
                    DrivesManager.getInstance().refreshLoginAttemps();
                    DriveUser.getInstance().setGoogle_id("");
                    DriveUser.getInstance().setDropbox_id("");
                }
                if (!btnGoogle.isActivated()) {
                    gdw.setOnConnectedAction(gdw.getSignoutAction());
                    btnGoogle.setActivated(true);
                }
                else {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(gac);
                    startActivityForResult(signInIntent, RESOLVE_CONNECTION_REQUEST_CODE);
                }
                break;
            case R.id.btnConnectDropbox:
                edited = true;
                if (edited) {
                    setResult(EventConst.LOGIN_SUCCESS_RESULT_CODE);
                    DrivesManager.getInstance().refreshLoginAttemps();
                    DriveUser.getInstance().setGoogle_id("");
                    DriveUser.getInstance().setDropbox_id("");
                }
                if (!btnDropbox.isActivated()) {
                    ddw.signOut();
                    btnDropbox.setActivated(true);
                }
                else {
                    com.dropbox.core.android.Auth.startOAuth2Authentication(this, getString(R.string.dbox_app_key));
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
                    if (result.isSuccess()) {
                        btnGoogle.setActivated(false);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String token = com.dropbox.core.android.Auth.getOAuth2Token();
        if (token != null){
            ddw.saveAccToken(this, token);
            btnDropbox.setActivated(false);
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }
}
