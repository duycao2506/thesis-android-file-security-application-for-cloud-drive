package thesis.tg.com.s_cloud.user_interface.activity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.framework_components.user_interface.activity.KasperActivity;
import thesis.tg.com.s_cloud.framework_components.utils.GlobalEventListennerDelegate;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.user_interface.fragment.TransferingTaskFragment;
import thesis.tg.com.s_cloud.utils.EventConst;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TransferingActivity extends KasperActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.fab_expand_menu_button).setVisibility(View.GONE);
        this.changeFragment(R.id.fragmentHolder,this.mainFragment,null);
    }

    @Override
    protected void initFragment() {
        super.initFragment();
        this.mainFragment = new TransferingTaskFragment();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_stateful;
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
