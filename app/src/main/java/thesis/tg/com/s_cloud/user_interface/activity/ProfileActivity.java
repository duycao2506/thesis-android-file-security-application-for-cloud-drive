package thesis.tg.com.s_cloud.user_interface.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static thesis.tg.com.s_cloud.utils.EventConst.RESOLVE_CONNECTION_REQUEST_CODE;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    CircleImageView civProfileAvatar;
    TextView tvNameHeader;
    Button btnGoogle, btnDropbox;
    GoogleApiClient gac;
    GoogleDriveWrapper gdw;
    DbxDriveWrapper ddw;
    private boolean edited = false;
    Intent resultintent;
    BaseApplication ba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ba = (BaseApplication) getApplication();
        setContentView(R.layout.activity_profile);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        gdw = (GoogleDriveWrapper) ba.getDriveWrapper(DriveType.GOOGLE);
        gac = gdw.getClient();
        ddw = (DbxDriveWrapper) ba.getDriveWrapper(DriveType.DROPBOX);
        resultintent = new Intent();
        setResult(EventConst.LOGIN_SUCCESS_RESULT_CODE,resultintent);

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
                ba.getDriveMannager().refreshLoginAttemps();
                if (!btnGoogle.isActivated()) {
                    resultintent.putExtra(EventConst.GOOGLE_CONNECT,false);
                    gdw.setOnConnectedAction(gdw.getSignoutAction());
                    btnGoogle.setActivated(true);
                }
                else {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(gac);
                    startActivityForResult(signInIntent, RESOLVE_CONNECTION_REQUEST_CODE);
                }
                break;
            case R.id.btnConnectDropbox:
                ba.getDriveMannager().refreshLoginAttemps();
                if (!btnDropbox.isActivated()) {
                    resultintent.putExtra(EventConst.DBX_CONNECT,false);
                    ddw.signOut();
                    ddw.saveAccToken(this, "");
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
                        resultintent.putExtra(EventConst.GOOGLE_CONNECT,true);
                        btnGoogle.setActivated(false);
                        gdw.setOnConnectedAction(null);
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
            resultintent.putExtra(EventConst.DBX_CONNECT,true);
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
