package thesis.tg.com.s_cloud.user_interface.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.from_third_party.dropbox.DbxDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleDriveWrapper;
import thesis.tg.com.s_cloud.entities.DriveUser;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.data.from_server.GeneralResponse;
import thesis.tg.com.s_cloud.framework_components.data.from_server.POSTRequestService;
import thesis.tg.com.s_cloud.framework_components.data.from_server.RequestService;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DataUtils;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;
import thesis.tg.com.s_cloud.utils.UiUtils;
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

    ProgressDialog generalPd;

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
        edtConfirmPass.setVisibility(View.GONE);
        edtPass.setVisibility(View.GONE);
        findViewById(R.id.tvPassword).setVisibility(View.GONE);

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
                        sb.append((selectedmonth+1));
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
            case R.id.confirm:
                updateProfile();
                break;
        }
        return true;
    }

    private void updateProfile() {
        generalPd = UiUtils.getDefaultProgressDialog(this,false,getString(R.string.updating));
        generalPd.show();
        JSONObject profJson = profileJSON();
        POSTRequestService prs = new POSTRequestService(this, RequestService.RequestServiceConstant.profile, new MyCallBack() {
            @Override
            public void callback(String message, int code, Object data) {
                GeneralResponse gr = (GeneralResponse) data;
                generalPd.dismiss();
                try {
                    JSONObject resp = new JSONObject(gr.getResponse());
                    if (resp.getString("status").compareTo("success") == 0){
                        Toast.makeText(ProfileActivity.this, getString(R.string.updateprofilesucc),Toast.LENGTH_LONG).show();
                        ba.getDriveUser().setBirthday(edtBirthday.getText().toString());
                        ba.getDriveUser().setCountry(edtCountry.getText().toString());
                        ba.getDriveUser().setJob(edtJob.getText().toString());
                        ba.getDriveUser().setName(edtFullname.getText().toString());
                        tvNameHeader.setText(ba.getDriveUser().getName());
                    }else{
                        Toast.makeText(ProfileActivity.this, getString(R.string.failt),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ProfileActivity.this, getString(R.string.failt),Toast.LENGTH_LONG).show();
                }
            }
        }, new GeneralResponse());
        Map<String, Object> headers = RequestService.getBaseHeaders();
        headers.put("Authorization", "Bearer " + ba.getDriveUser().getAccesstoken());
        prs.setHeaders(headers);
        prs.setJsonObject(profJson);
        prs.executeService();
    }

    private JSONObject profileJSON() {
        JSONObject jso = new JSONObject();
        try{
            jso.put("birthday",edtBirthday.getText().toString());
            jso.put("job", edtJob.getText().toString());
            jso.put("country",edtCountry.getText().toString());
            jso.put("fullname",edtFullname.getText().toString());
            jso.put("mac_address", DataUtils.getMacAddress("wlan0",this));
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jso;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_has_confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
