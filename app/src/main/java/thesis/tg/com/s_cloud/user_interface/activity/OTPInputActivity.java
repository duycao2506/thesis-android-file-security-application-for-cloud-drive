package thesis.tg.com.s_cloud.user_interface.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.data.from_server.GeneralResponse;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.security.SimpleRSACipher;
import thesis.tg.com.s_cloud.utils.EventConst;

public class OTPInputActivity extends AppCompatActivity {


    EditText edtOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpinput);
        //Toolbar
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle(R.string.device_auth);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edtOTP = (EditText) findViewById(R.id.edtOTP);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_has_confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
            case R.id.confirm:
                final BaseApplication ba = (BaseApplication) this.getApplicationContext();
                Intent intent = getIntent();
                final String authtoken = intent.getStringExtra("auth_token");
                String otp = edtOTP.getText().toString();
                final String mac = intent.getStringExtra("mac_addr");
                if (otp.length() < 6){
                    Toast.makeText(this, R.string.wrongformatotp,Toast.LENGTH_SHORT).show();
                    break;
                }
                try {
                    LoginActivity.requestForAuthorize(this, authtoken, otp, mac, new MyCallBack() {
                        @Override
                        public void callback(String message, int code, Object data) {
                            GeneralResponse gr = (GeneralResponse) data;
                            if (!gr.isResponseError()){
                                String json = gr.getResponse();
                                JSONObject requestdeviceauthResp;
                                try {
                                    requestdeviceauthResp = new JSONObject(json);
                                    Log.e("REQAUT", requestdeviceauthResp.toString());
                                    if (requestdeviceauthResp.getString("status").compareTo("success")==0){
                                        String keyraw = requestdeviceauthResp.getString("key");
                                        SimpleRSACipher rsac = ba.getSimpleCipher();
                                        String deMainKey = rsac.decryptKey(keyraw);
                                        JSONObject rootrequesjso =
                                                LoginActivity.getRootAssignRequestJSONObj(
                                                        ba,
                                                        new JWT(authtoken),
                                                        mac,
                                                        deMainKey);
                                        rootrequesjso.put("is_root",false);
                                        LoginActivity.requestRoot(OTPInputActivity.this,
                                                authtoken,
                                                rootrequesjso,
                                                new MyCallBack() {
                                            @Override
                                            public void callback(String message, int code, Object data) {
                                                switch (message){
                                                    case EventConst.LOGIN_FAIL:
                                                        Toast.makeText(OTPInputActivity.this,getString(R.string.failt),Toast.LENGTH_LONG).show();
                                                        break;
                                                    case EventConst.LOGIN_SUCCESS:
                                                        setResult(EventConst.OTP_RESULT_SUCCESS);
                                                        break;
                                                }
                                            }
                                        });
                                    }else{
                                        Toast.makeText(OTPInputActivity.this,"Wrong OTP code",Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
        }
        return true;
    }

}
