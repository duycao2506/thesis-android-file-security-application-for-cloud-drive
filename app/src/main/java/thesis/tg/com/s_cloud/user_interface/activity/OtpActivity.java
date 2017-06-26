package thesis.tg.com.s_cloud.user_interface.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.BaseViewAnimator;
import com.daimajia.androidanimations.library.YoYo;
import com.google.common.util.concurrent.AbstractScheduledService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.framework_components.utils.ResizeAnimation;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OtpActivity extends AppCompatActivity {

    TextView tvOtpCode;
    View progressOutSide, progressInside;
    String[] arrColor;
    private ScheduledExecutorService scheduler;
    private Handler handlerTiming = new Handler() {
        int count = 0;
        public void handleMessage(Message msg) {
            count = (count + 1 ) % minute;
            changeColorProgress(count);
            Animation anim = new ResizeAnimation(progressInside,fixwidth,progressInside.getHeight(),progressOutSide.getWidth(),progressOutSide.getHeight(),19000);
            anim.setFillAfter(false);
            progressInside.startAnimation(anim);
//            updateTimeProgress();
//            int min1 = count/interval;
//            if (min1 != min){
//                min = min1;
////
//            }
            changeOtp();
        }
    };


    private int fixwidth;
    private int minute = 6;
    private int interval = 200;


    private void changeColorProgress(int colurnum) {
        progressOutSide.setBackgroundColor(Color.parseColor(arrColor[colurnum]));
        progressInside.setBackgroundColor(Color.parseColor(arrColor[colurnum+1]));
    }



    private void updateTimeProgress() {
        int curWidth = this.progressInside.getWidth();
        curWidth = curWidth % fixwidth + fixwidth/interval + 1;
        this.progressInside.getLayoutParams().width = curWidth;

        this.progressInside.setLayoutParams(new LinearLayout.LayoutParams(curWidth,this.progressInside.getHeight()));
    }

    private void changeOtp() {

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
        progressInside = findViewById(R.id.progressInside);
        progressOutSide = findViewById(R.id.progressOutside);
        arrColor = getResources().getStringArray(R.array.colorChangeArray);
        fixwidth = progressInside.getLayoutParams().width;
        scheduler =
                Executors.newSingleThreadScheduledExecutor();
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




}
