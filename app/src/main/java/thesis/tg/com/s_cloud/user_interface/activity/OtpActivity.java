package thesis.tg.com.s_cloud.user_interface.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.daimajia.androidanimations.library.BaseViewAnimator;
import com.daimajia.androidanimations.library.YoYo;
import com.google.common.util.concurrent.AbstractScheduledService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.data.from_server.GeneralResponse;
import thesis.tg.com.s_cloud.framework_components.data.from_server.POSTRequestService;
import thesis.tg.com.s_cloud.framework_components.data.from_server.RequestService;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.framework_components.utils.ResizeAnimation;
import thesis.tg.com.s_cloud.security.AESCipher;
import thesis.tg.com.s_cloud.security.RSAEncryptor;
import thesis.tg.com.s_cloud.security.SimpleRSACipher;
import thesis.tg.com.s_cloud.utils.DataUtils;
import thesis.tg.com.s_cloud.utils.EventConst;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OtpActivity extends AppCompatActivity {

    TextView tvOtpCode;
    View progressOutSide, progressInside;
    String[] arrColor;
    private ScheduledExecutorService scheduler;
    String modulus;
    String exponent;
    private Handler handlerTiming = new Handler() {
        int count = 0;
        public boolean init = true;
        public void handleMessage(Message msg) {
            changeColorProgress(count);
            if (count==0 && !init){
                scheduler.shutdown();
                changeOtp();
                init = true;
                return;
            }
            this.init = false;

            Animation anim = new ResizeAnimation(
                    progressInside,
                    0,
                    progressOutSide.getLayoutParams().height,
                    progressOutSide.getLayoutParams().width,
                    progressOutSide.getLayoutParams().height,
                    19600);
            anim.setFillAfter(false);
            progressInside.startAnimation(anim);

            count = (count + 1 ) % minute;


        }
    };


    private int fixwidth;
    private int minute = 6;
    private int interval = 200;


    private void changeColorProgress(int colurnum) {
        progressOutSide.setBackgroundColor(Color.parseColor(arrColor[colurnum]));
        progressInside.setBackgroundColor(Color.parseColor(arrColor[colurnum+1]));
    }

    private void changeOtp() {
        try {
            requestForOTP(this, new MyCallBack() {
                @Override
                public void callback(String message, int code, Object data) {
                    if (code == 1) {
                        String otp = data.toString();
                        int otplength = otp.length();
                        if (otplength < 6) {
                            for (int i = 0; i < 6 - otplength; i++)
                                otp = "0" + otp;
                        }
                        tvOtpCode.setText(otp);
                        scheduler = Executors.newSingleThreadScheduledExecutor();
                        scheduler.scheduleAtFixedRate(new Runnable() {
                            @Override
                            public void run() {
                                handlerTiming.obtainMessage(0).sendToTarget();
                            }
                        },0,20000, TimeUnit.MILLISECONDS);
                    }
                    else{
                        Toast.makeText(OtpActivity.this,getString(R.string.unknerr),Toast.LENGTH_LONG).show();
                    }
                }
            },modulus,exponent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(OtpActivity.this,getString(R.string.unknerr),Toast.LENGTH_LONG).show();
        }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        //Toolbar
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle(R.string.get_otp);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Views Manipulation
        tvOtpCode = (TextView) findViewById(R.id.tvOtpCode);

        tvOtpCode.setText(getIntent().getStringExtra("FirstOTP"));
        this.modulus = getIntent().getStringExtra("modulus");
        this.exponent = getIntent().getStringExtra("exponent");

//        final BaseApplication ba = (BaseApplication) this.getApplicationContext();
//        try {
//            LoginActivity.requestForAuthorize(this, ba.getDriveUser().getAccesstoken(), tvOtpCode.getText().toString(), "11:22:33:44:55:66", new MyCallBack() {
//                @Override
//                public void callback(String message, int code, Object data) {
//                    GeneralResponse gr = (GeneralResponse) data;
//                    String json = gr.getResponse();
//                    JSONObject requestdeviceauthResp;
//                    try {
//                        requestdeviceauthResp = new JSONObject(json);
//                        Log.e("REQAUT", requestdeviceauthResp.toString());
//                        if (requestdeviceauthResp.getString("status").compareTo("success")==0){
//
//                            SimpleRSACipher rsac = new SimpleRSACipher("11:22:33:44:55:66", Build.BRAND, Build.MODEL);
//                            String keyraw = new JSONObject(requestdeviceauthResp.getString("data")).getString("key");
//                            String deMainKey = rsac.decryptKey(keyraw);
//                            Log.e("A", keyraw);
//                            Log.e("B", deMainKey);
//                            JSONObject rootrequesjso =
//                                    LoginActivity.getRootAssignRequestJSONObj(
//                                            ba,
//                                            new JWT(ba.getDriveUser().getAccesstoken()),
//                                            "11:22:33:44:55:66",
//                                            deMainKey);
//                            rootrequesjso.put("is_root","False");
//                            LoginActivity.requestRoot(OtpActivity.this,
//                                    ba.getDriveUser().getAccesstoken(),
//                                    rootrequesjso,
//                                    new MyCallBack() {
//                                        @Override
//                                        public void callback(String message, int code, Object data) {
//                                            switch (message){
//                                                case EventConst.LOGIN_FAIL:
//                                                    Toast.makeText(OtpActivity.this,getString(R.string.failt),Toast.LENGTH_LONG).show();
//                                                    break;
//                                                case EventConst.LOGIN_SUCCESS:
//                                                    Toast.makeText(OtpActivity.this,"cool",Toast.LENGTH_LONG).show();
//                                                    break;
//                                            }
//                                        }
//                                    });
//                        }else{
//                            Toast.makeText(OtpActivity.this,"Wrong OTP code",Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        progressInside = findViewById(R.id.progressInside);
        progressOutSide = findViewById(R.id.progressOutside);
        arrColor = getResources().getStringArray(R.array.colorChangeArray);
        fixwidth = progressInside.getLayoutParams().width;

        scheduler =
                Executors.newSingleThreadScheduledExecutor();

    }

    @Override
    protected void onStart() {
        super.onStart();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                handlerTiming.obtainMessage(0).sendToTarget();
            }
        },0,20000, TimeUnit.MILLISECONDS);

    }



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public static void requestForOTP(final Context context, final MyCallBack caller, String modulus, String exponent) throws Exception {
        final BaseApplication ba = (BaseApplication) context.getApplicationContext();
        POSTRequestService prs = new POSTRequestService(context, RequestService.RequestServiceConstant.request_for_otp, new MyCallBack() {
            @Override
            public void callback(String message, int code, Object data) {
                GeneralResponse gr = (GeneralResponse) data;
                try {
                    JSONObject jso = new JSONObject(gr.getResponse());
                    if (!jso.isNull("code")){
                        String a = jso.get("code").toString();
                        BigInteger a2 = new BigInteger(a);
                        String otp = ba.getSimpleCipher().decryptOTP(a2.toString());
                        if (otp.length() > 6)
                            caller.callback(message,0,ba.getString(R.string.not_root));
                        else
                            caller.callback(message,1,otp);
                    }else{
                        caller.callback(message,0,jso.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    caller.callback(message,0,context.getString(R.string.unknerr));
                }
            }
        }, new GeneralResponse());
        JSONObject jso = new JSONObject();
        jso.put("mac_address", DataUtils.getMacAddress("wlan0",context));
        String decryptedMainKey = ba.getSimpleCipher().decryptKey(ba.getDriveUser().getMainKey());
        byte[] mod = new BigInteger(modulus).toByteArray();
        byte[] exp = new BigInteger(exponent).toByteArray();
        String encryptedMainKey = new RSAEncryptor(Base64.encodeToString(mod,Base64.NO_WRAP),
                Base64.encodeToString(exp,Base64.NO_WRAP)).encrypt(decryptedMainKey);
        jso.put("encrypted_key",encryptedMainKey);
        prs.setJsonObject(jso);
        Log.e("ERR",jso.toString());
        Log.e("ACCTOKEN",decryptedMainKey);
        Log.e("ACCTOKENE",encryptedMainKey);
        Log.e("MOD", modulus);
        Log.e("EXP",exponent);

        Map<String, Object > headers = RequestService.getBaseHeaders();
        headers.put("Authorization", "Bearer " + ba.getDriveUser().getAccesstoken());
        prs.setHeaders(headers);
        prs.executeService();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        scheduler.shutdown();
    }
}
