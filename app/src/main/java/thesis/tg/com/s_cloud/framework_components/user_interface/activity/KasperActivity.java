package thesis.tg.com.s_cloud.framework_components.user_interface.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.framework_components.user_interface.fragment.RecycleViewFragment;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;


public class KasperActivity extends AppCompatActivity implements MyCallBack {




    public static final String FINISH = "F";
    public static final String START = "S";
    public static final String SNACKSHOW = "SS";

    protected Fragment mainFragment;
    protected Snackbar snackbarNoti;
    protected CoordinatorLayout cl;
    protected String notice;
    private View loadingView;


    protected int loadingViewId;
    protected Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentLayout());
        toolbar = (Toolbar) findViewById(getToolbarRes());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initFragment();

        cl = (CoordinatorLayout) findViewById(R.id.coordinateLayout);
        loadingView = findViewById(getLoadingViewId());
        loadingView.setVisibility(View.GONE);
    }

    /**
     * Replace by brand new fragment
     * @param newFragment
     */
    protected void changeFragment(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolder, newFragment,"ROOT");
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++)
            getSupportFragmentManager().popBackStackImmediate();
        transaction.addToBackStack("ROOT");
        transaction.commit();
    }

    /**
     * Fragment Backstack
     * @param newFragment
     * @param name
     */
    protected void addFragmentToStack(Fragment newFragment, String name){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        String tag = name + getSupportFragmentManager().getBackStackEntryCount();
        transaction.replace(R.id.fragmentHolder, newFragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    /**
     * Custom Main Fragment, must override
     */
    protected void initFragment() {
        mainFragment = new RecycleViewFragment();
    }


    /**
     *
     * Loading Screen Display or not
     * @param message
     * @param code
     * @param data
     */
    @Override
    public void callback(String message, int code, Object data) {
        switch (message){
            case START:
                loadingView.setVisibility(View.VISIBLE);
                break;
            case FINISH:
                loadingView.setVisibility(View.GONE);
                break;

            case SNACKSHOW:
                if (snackbarNoti != null)
                    snackbarNoti.show();
                break;

        }
    }

    /**
     * Custom Loading Screen
     * @return
     */
    protected int getLoadingViewId() {
        return R.id.loadingScreen;
    }

    /**
     * Custom Content Layout
     * @return
     */
    protected int getContentLayout(){
        return R.layout.activity_stateful;
    }

    /**
     * Custom Toolbar
     * @return
     */
    public int getToolbarRes() {
        return R.id.toolbar;
    }



    /**
     *
     * Fragment Backstack change
     */


}
