package thesis.tg.com.s_cloud.user_interface.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.from_third_party.dropbox.DbxDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleDriveWrapper;
import thesis.tg.com.s_cloud.entities.DriveUser;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
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


    //Profile View

    EditText edtFullname, edtJob, edtCountry, edtBirthday, edtEmail, edtConfirmPass, edtPass;

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
        setResult(EventConst.LOGIN_SUCCESS_RESULT_CODE, resultintent);

        assignViews();
        pourDataOnViews();



        setUpView();
    }

    private void pourDataOnViews() {
        this.edtBirthday.setText(ba.getDriveUser().getBirthday());
        this.edtCountry.setText(ba.getDriveUser().getCountry());
        this.edtEmail.setText(ba.getDriveUser().getEmail());
        this.edtFullname.setText(ba.getDriveUser().getName());
        this.edtJob.setText(ba.getDriveUser().getJob());
    }

    private void assignViews() {
        edtFullname = (EditText) findViewById(R.id.edtProfileName);

        edtBirthday = (EditText) findViewById(R.id.edtProfileBirthday);
        edtBirthday.setOnClickListener(this);
        edtBirthday.setFocusable(false);


        edtCountry = (EditText) findViewById(R.id.edtProfileCountry);

        edtEmail = (EditText) findViewById(R.id.edtProfileEmail);
        edtEmail.setEnabled(false);

        edtJob = (EditText) findViewById(R.id.edtProfileJob);

        edtConfirmPass = (EditText) findViewById(R.id.edtProfilePasswordCnf);
        edtPass = (EditText) findViewById(R.id.edtProfilePassword);
    }

    private void setUpView() {
        civProfileAvatar = (CircleImageView) findViewById(R.id.ivProfileAvatar);
        if (ba.getDriveUser().getAvatarLink() != null
                && ba.getDriveUser().getAvatarLink().length() > 0)
            Picasso.with(this).load(ba.getDriveUser().getAvatarLink()).into(civProfileAvatar);
        else if (ba.getDriveUser().getAvatar() != null)
            Picasso.with(this).load(ba.getDriveUser().getAvatar()).into(civProfileAvatar);
        else
            civProfileAvatar.setImageResource(ba.getDriveUser().getDefaultAvatar());
        tvNameHeader = (TextView) findViewById(R.id.tvHeaderName);
        tvNameHeader.setText(ba.getDriveUser().getName());
        btnGoogle = (Button) findViewById(R.id.btnConnectGoogle);
        btnGoogle.setActivated(!ba.getDriveUser().isSignedIn(DriveType.GOOGLE));
        btnGoogle.setOnClickListener(this);
        btnDropbox = (Button) findViewById(R.id.btnConnectDropbox);
        btnDropbox.setActivated(!ba.getDriveUser().isSignedIn(DriveType.DROPBOX));
        btnDropbox.setOnClickListener(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnConnectGoogle:
                ba.getDriveMannager().refreshLoginAttemps();
                if (!btnGoogle.isActivated()) {
                    resultintent.putExtra(EventConst.GOOGLE_CONNECT, false);
                    gdw.signOut(new MyCallBack() {
                        @Override
                        public void callback(String message, int code, Object data) {
                            if ((boolean) data)
                                btnGoogle.setActivated(true);
                            else
                                gdw.setOnConnectedAction(gdw.getSignoutAction());
                            DriveUser.getInstance(ba).setId(DriveType.GOOGLE,null);
                        }
                    });
                } else {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(gac);
                    startActivityForResult(signInIntent, RESOLVE_CONNECTION_REQUEST_CODE);
                }
                break;
            case R.id.btnConnectDropbox:
                ba.getDriveMannager().refreshLoginAttemps();
                if (!btnDropbox.isActivated()) {
                    resultintent.putExtra(EventConst.DBX_CONNECT, false);
                    ddw.signOut(new MyCallBack() {
                        @Override
                        public void callback(String message, int code, Object data) {
                            ddw.saveAccToken(ProfileActivity.this, "");
                            DriveUser.getInstance(ba).setId(DriveType.DROPBOX,null);
                            btnDropbox.setActivated(true);
                        }
                    });
                } else {
                    com.dropbox.core.android.Auth.startOAuth2Authentication(this, getString(R.string.dbox_app_key));
                }
                break;
            case R.id.edtProfileBirthday:
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                    /*      Your code   to get date and time    */
                        StringBuilder sb = new StringBuilder();
                        sb.append(selectedday);
                        sb.append("/");
                        sb.append(selectedmonth);
                        sb.append("/");
                        sb.append(selectedyear);
                        edtBirthday.setText(sb.toString());
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle(getString(R.string.se_date));
                mDatePicker.show();
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
                        resultintent.putExtra(EventConst.GOOGLE_CONNECT, true);
                        DriveUser.getInstance(ba).setId(DriveType.GOOGLE,null);
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
        if (token != null) {
            ddw.saveAccToken(this, token);
            btnDropbox.setActivated(false);
            resultintent.putExtra(EventConst.DBX_CONNECT, true);
            DriveUser.getInstance(ba).setId(DriveType.DROPBOX,null);
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
