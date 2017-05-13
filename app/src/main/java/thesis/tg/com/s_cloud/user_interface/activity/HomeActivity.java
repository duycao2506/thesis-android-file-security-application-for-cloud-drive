package thesis.tg.com.s_cloud.user_interface.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.api.client.util.Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.from_third_party.GoogleDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.GoogleUploadTask;
import thesis.tg.com.s_cloud.framework_components.user_interface.activity.KasperActivity;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.user_interface.fragment.FileListFragment;
import thesis.tg.com.s_cloud.utils.DataUtils;
import thesis.tg.com.s_cloud.utils.EventConst;
import thesis.tg.com.s_cloud.utils.SFileInputStream;
import thesis.tg.com.s_cloud.utils.UiUtils;

import static thesis.tg.com.s_cloud.user_interface.fragment.FileListFragment.ViewMode.GRID;
import static thesis.tg.com.s_cloud.user_interface.fragment.FileListFragment.ViewMode.LIST;
import static thesis.tg.com.s_cloud.utils.EventConst.LOGIN_REQUEST_CODE;

public class HomeActivity extends KasperActivity implements
        NavigationView.OnNavigationItemSelectedListener
{


    public interface FragmentStackName{
        String FILES = "FILES";
    }


    private DrawerLayout drawer;
    private NavigationView navigationView;
    private FloatingActionButton fab;
    ActionBarDrawerToggle toggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);


        //Sign int automatically first
        final GoogleApiClient gac = GoogleDriveWrapper
                .getInstance()
                .googleSignInServiceInit(this);
        OptionalPendingResult<GoogleSignInResult> op = Auth.GoogleSignInApi.silentSignIn(gac);
        op.setResultCallback(new ResultCallback<GoogleSignInResult>() {
            @Override
            public void onResult(@NonNull GoogleSignInResult googleSignInResult) {

                GoogleDriveWrapper.getInstance()
                        .handleSignInResult(googleSignInResult, HomeActivity.this);
            }
        });


        fab = (FloatingActionButton) findViewById(R.id.fbtnAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });




        setupNavigationDrawer();
    }


    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    EventConst.FILE_SELECT_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void initFragment() {
        this.mainFragment = new FileListFragment();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    public int getToolbarRes() {
        return R.id.toolbar;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                doHomeEvent();
                break;
            case R.id.viewmode:
                changeViewMode(item);

                break;
        }
        return true;
    }

    private FileListFragment getTopFragment(){
        String tag = FragmentStackName.FILES
                +(getSupportFragmentManager().getBackStackEntryCount()-1);


        FileListFragment ff = (FileListFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (ff == null){
            ff = (FileListFragment) getSupportFragmentManager().findFragmentByTag("ROOT");
        }
        return ff;
    }

    private void changeViewMode(MenuItem item) {
        FileListFragment ff = getTopFragment();

        if (ff.getVm() == GRID) {
            ff.changeViewMode(LIST);
            item.setIcon(R.drawable.ic_grid);
        }
        else {
            item.setIcon(R.drawable.ic_list);
            ff.changeViewMode(GRID);
        }
    }

    private void doHomeEvent() {
        int countStack = getSupportFragmentManager().getBackStackEntryCount();
        if (countStack > 1)
        {
            getSupportFragmentManager().popBackStack();
            if (countStack == 2)
                toggle.setDrawerIndicatorEnabled(true);
        }else{
            drawer.openDrawer(GravityCompat.START);
        }
    }


    /**
     * Navigation Drawer Area
     */

    private void setupNavigationDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        toggle.setDrawerSlideAnimationEnabled(true);


        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.inflateMenu(R.menu.nav_menu);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }


    @Override
    public void callback(String message, int code, Object data) {
        super.callback(message, code, data);
        switch (message){
            case EventConst.OPEN_FOLDER:
                toggle.setDrawerIndicatorEnabled(false);
                FileListFragment fileListFragment = new FileListFragment();
                this.addFragmentToStack(fileListFragment,FragmentStackName.FILES);
                this.callback(HomeActivity.START,1,null);
                fileListFragment.loadRefresh(this);
                break;
            case EventConst.LOGIN_FAIL:
                Intent intent = new Intent(this,LoginActivity.class);
                startActivityForResult(intent, EventConst.LOGIN_REQUEST_CODE);
                break;
            case EventConst.LOGIN_SUCCESS:
                this.callback(HomeActivity.START,1,null);
                getTopFragment().loadRefresh(this);
                break;
            default:
                break;
        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        GoogleDriveWrapper.getInstance().getClient().disconnect();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case LOGIN_REQUEST_CODE:
                switch (resultCode){
                    case EventConst.LOGIN_CANCEL_RESULT_CODE:
                        finish();
                        break;
                    case EventConst.LOGIN_SUCCESS_RESULT_CODE:
                        this.callback(HomeActivity.START,1,null);
                        getTopFragment().loadRefresh(this);
                        break;
                    default:
                        break;
                }
                break;
            case EventConst.FILE_SELECT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    SFileInputStream sfis;
                    Uri uri = data.getData();
                    Log.d("TAG", "File Uri: " + uri.toString());
                    // Get the path
                    String path = DataUtils.getPath(this,uri);
                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String type = mime.getExtensionFromMimeType(getContentResolver().getType(uri));
                    GoogleUploadTask gut = new GoogleUploadTask(GoogleDriveWrapper.getInstance().getDriveService());
                    if (path == null || path.length() == 0)
                    {
                        Toast.makeText(this, "Cannot access this file", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    gut.start(type,path, new MyCallBack() {
                        @Override
                        public void callback(String message, int code, Object data) {
                            Toast.makeText(HomeActivity.this, "Upload", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.d("TAG", "File Path: " + path);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        int countStack = getSupportFragmentManager().getBackStackEntryCount();
        if (countStack == 1)
            UiUtils.showExitAlertDialog(this,0);
        else {
            if (countStack == 2)
                toggle.setDrawerIndicatorEnabled(true);
            super.onBackPressed();
        }
    }
}
