package thesis.tg.com.s_cloud.user_interface.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.DriveWrapper;
import thesis.tg.com.s_cloud.data.DrivesManager;
import thesis.tg.com.s_cloud.data.from_third_party.dropbox.DbxDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleUploadTask;
import thesis.tg.com.s_cloud.entities.DriveUser;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.entities.SDriveFolder;
import thesis.tg.com.s_cloud.framework_components.user_interface.activity.KasperActivity;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.user_interface.fragment.FileListFragment;
import thesis.tg.com.s_cloud.utils.DataUtils;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;
import thesis.tg.com.s_cloud.utils.UiUtils;

import static thesis.tg.com.s_cloud.user_interface.fragment.FileListFragment.ViewMode.GRID;
import static thesis.tg.com.s_cloud.user_interface.fragment.FileListFragment.ViewMode.LIST;
import static thesis.tg.com.s_cloud.utils.EventConst.LOGIN_REQUEST_CODE;

public class HomeActivity extends KasperActivity implements
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


    SparseArray fragmentNavigator;
    FileListFragment[] fileListFragments = new FileListFragment[DrivesManager.getInstance().getNumDrive()];


    private Menu menu;
    private FileListFragment topFragment;




    public interface FragmentStackName{
        String FILES = "FILES";
    }

    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private FloatingActionButton fab;
    private MyCallBack signInCallback = new MyCallBack() {
        int failTimes = DrivesManager.getInstance().getNumDrive();
        @Override
        synchronized public void callback(String message, int code, Object data) {
            switch (message) {
                case EventConst.LOGIN_FAIL:
                    failTimes--;
                    if (failTimes > 0) return;
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    failTimes = 0;
                    break;
                case EventConst.LOGIN_SUCCESS:
                    if (topFragment != null)
                        return;
                    HomeActivity.this.callback(HomeActivity.START, 1, null);
                    updateNavHeader();
                    topFragment = (FileListFragment) fragmentNavigator.get(code);
                    changeFragment(topFragment);
                    topFragment.loadRefresh(HomeActivity.this);
                    break;
            }
        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Create Fragment Navigator
        this.fragmentNavigator = new SparseArray<Fragment>();


        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);



        //Dropbox
        DbxDriveWrapper.getInstance().signIn(this, signInCallback);



        //Sign int automatically first
        final GoogleApiClient gac = GoogleDriveWrapper
                .getInstance()
                .googleSignInServiceInit(this);

        OptionalPendingResult<GoogleSignInResult> op = Auth.GoogleSignInApi.silentSignIn(gac);
        op.setResultCallback(new ResultCallback<GoogleSignInResult>() {
            @Override
            public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                GoogleDriveWrapper.getInstance()
                        .handleSignInResult(googleSignInResult, signInCallback);
            }
        });


        fab = (FloatingActionButton) findViewById(R.id.fbtnAdd);
        fab.setOnClickListener(this);






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
        fragmentNavigator.put(DriveType.GOOGLE,new FileListFragment.Builder()
                .setFragmentName(getString(R.string.g_drive))
                .setDriveType(DriveType.GOOGLE)
                .setViewMode(LIST)
                .build() );
        fragmentNavigator.put(DriveType.DROPBOX, new FileListFragment.Builder()
                .setFragmentName(getString(R.string.dbox))
                .setDriveType(DriveType.DROPBOX)
                .setViewMode(LIST)
                .build());
        fragmentNavigator.put(DriveType.LOCAL, new FileListFragment.Builder()
                .setFragmentName(getString(R.string.local_storage))
                .setDriveType(DriveType.LOCAL)
                .setViewMode(LIST)
                .build());
        getSupportActionBar().setTitle(R.string.app_name);
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
        this.menu = menu;
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
                changeViewMode(item, topFragment);
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

    private void changeViewMode(MenuItem item, FileListFragment ff) {
        String mode = item.getTitle().toString();
        if (mode.equalsIgnoreCase(getString(R.string.grid_mode))) {
            ff.changeViewMode(LIST);
            item.setIcon(R.drawable.ic_grid);
            item.setTitle(getString(R.string.list_mode));
        }
        else {
            item.setIcon(R.drawable.ic_list);
            ff.changeViewMode(GRID);
            item.setTitle(getString(R.string.grid_mode));
        }
    }

    private void doHomeEvent() {
        int countStack = getSupportFragmentManager().getBackStackEntryCount();
        if (countStack > 1)
        {
            getSupportFragmentManager().popBackStackImmediate();
            if (countStack == 2)
                toggle.setDrawerIndicatorEnabled(true);
            backFolderHandle();
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
        View header = navigationView.getHeaderView(0);
        header.setOnClickListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.local:
            case R.id.gdrive:
            case R.id.dbox:
                changeDrive(id);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeDrive(int id) {
        FileListFragment fragment;
        fragment = (FileListFragment) fragmentNavigator.get(id);
        if (fragment.getDriveType() == topFragment.getDriveType())
            return;
        if (fragment == null)
        {
            fragment = new FileListFragment.Builder()
                    .setViewMode(topFragment.getVm())
                    .setDriveType(id)
                    .setFragmentName(DrivesManager.getInstance().getDriveName(this,id))
                    .build();
            fragmentNavigator.put(id, fragment);
        }
        getSupportActionBar().setTitle(fragment.getFragmentName());
        DriveWrapper.getInstance(id).resetListFileTask();
        topFragment = fragment;

        changeFragment(fragment);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.hdProfile:
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.fbtnAdd:
                showFileChooser();
                break;
            default:
                break;
        }
    }


    @Override
    public void callback(String message, int code, Object data) {
        super.callback(message, code, data);
        switch (message){
            case EventConst.OPEN_FOLDER:
                toggle.setDrawerIndicatorEnabled(false);
                SDriveFolder sdfo = (SDriveFolder) data;

                DriveWrapper.getInstance(topFragment.getDriveType()).addNewListFileTask(sdfo.getId());
                //Create new fragment
                topFragment = new FileListFragment.Builder()
                                .setDriveType(topFragment
                                .getDriveType())
                                .setFragmentName(sdfo.getName())
                                .setViewMode(topFragment.getVm())
                                .build();
                this.addFragmentToStack(topFragment,FragmentStackName.FILES);
                this.callback(HomeActivity.START,1,null);
                topFragment.loadRefresh(this);
                getSupportActionBar().setTitle(topFragment.getFragmentName());
                break;
            default:
                break;
        }
    }

    private void updateNavHeader() {
        TextView tvname = (TextView) navigationView.findViewById(R.id.tvUserName);
        CircleImageView civAvatar = (CircleImageView) navigationView.findViewById(R.id.imvAvatarUser);
        tvname.setText(DriveUser.getInstance().getName());
        Picasso.with(this).load(DriveUser.getInstance().getAvatar()).into(civAvatar);
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
            case EventConst.FILE_SELECT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d("TAG", "File Uri: " + uri.toString());
                    // Get the path
                    String path = DataUtils.getPath(this,uri);
                    String type = getContentResolver().getType(uri);
                    SDriveFile sdf = new SDriveFile(new File(path),type);
                    sdf.setCloud_type(DriveType.LOCAL);


                    if (path == null || path.length() == 0)
                    {
                        Toast.makeText(this, "Cannot access this file", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        //TODO: maybe sycned
                        DrivesManager.getInstance().transferDataTo(DriveType.GOOGLE,sdf,false);
                    }
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
            backFolderHandle();
        }
    }


    private void backFolderHandle(){
        this.callback(HomeActivity.FINISH,1,null);
        topFragment = getTopFragment();
        getSupportActionBar().setTitle(topFragment.getFragmentName());
        DriveWrapper.getInstance(topFragment.getDriveType()).popListFileTask();
    }
}
