package thesis.tg.com.s_cloud.user_interface.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import com.daimajia.androidanimations.library.Techniques;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.picasso.Picasso;
import java.io.File;
import de.hdodenhof.circleimageview.CircleImageView;
import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.CloudDriveWrapper;
import thesis.tg.com.s_cloud.data.DrivesManager;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleDriveWrapper;
import thesis.tg.com.s_cloud.entities.DriveUser;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.entities.SDriveFolder;
import thesis.tg.com.s_cloud.framework_components.user_interface.activity.KasperActivity;
import thesis.tg.com.s_cloud.framework_components.utils.EventBroker;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.user_interface.fragment.FileListFragment;
import thesis.tg.com.s_cloud.user_interface.fragment.FolderPathFragment;
import thesis.tg.com.s_cloud.user_interface.fragment.NotConnectedCloudFragment;
import thesis.tg.com.s_cloud.utils.DataUtils;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;
import thesis.tg.com.s_cloud.utils.ResourcesUtils;
import thesis.tg.com.s_cloud.utils.UiUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import static thesis.tg.com.s_cloud.user_interface.fragment.FileListFragment.ViewMode.GRID;
import static thesis.tg.com.s_cloud.user_interface.fragment.FileListFragment.ViewMode.LIST;

public class HomeActivity extends KasperActivity implements
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final String ROOT_TAG = "ROOT_FOLDER";

    private SparseArray fragmentNavigator;
    private FileListFragment[] fileListFragments = new FileListFragment[DrivesManager.getInstance().getNumDrive()];
    private NotConnectedCloudFragment notconnectedFragment;

    private Menu menu;
    private FileListFragment topFragment;
    private FolderPathFragment folderPathFragment;
    private NavigationView navigationView;


    public interface FragmentStackName{
        String FILES = "FILES";
    }

    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private View header;
    private FloatingActionButton fab;
    private FloatingActionsMenu fabmenu;

    private ProgressDialog generalProgressDialog;
    private MyCallBack signInCallback = new MyCallBack() {
        @Override
        synchronized public void callback(String message, int code, Object data) {
            switch (message) {
                case EventConst.LOGIN_FAIL:
                    DrivesManager.getInstance().setFailLogin(code);
                case EventConst.RELOGIN_FAIL:
                    if (!DrivesManager.getInstance().isTriedLoginAll()) return;
                    if (topFragment == null){
                        changeToNotConnected(0);
                    }else{
                        if (topFragment.getDriveType() == code)
                            changeToNotConnected(code);
                    }
                    if (DriveUser.getInstance().isSignedIn()) return;

                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case EventConst.LOGIN_SUCCESS:
                    DrivesManager.getInstance().setSuccessLogin(code);
                case EventConst.RELOGIN_SUCCESS:
                    updateNavHeader();
                    FileListFragment flf = (FileListFragment) fragmentNavigator.get(code);
                    MyCallBack callerRefresh = null;
                    if (topFragment == null) {
                        HomeActivity.this.callback(HomeActivity.START, 1, null);
                        topFragment = flf;
                        callerRefresh = HomeActivity.this;
                        if (!folderPathFragment.isAdded())
                            changeFragment(R.id.app_bar_extension,folderPathFragment,null);
                        folderPathFragment.refreshWithFolder(topFragment.getFragmentName());
                        changeFragment(R.id.fragmentHolder,flf,ROOT_TAG);
                        navigationView.setCheckedItem(code);
                    }
                    flf.loadRefresh(callerRefresh);
                    break;
            }
            if (DrivesManager.getInstance().isTriedLoginAll()){
                if (generalProgressDialog==null)
                    return;
                generalProgressDialog.dismiss();
                generalProgressDialog = null;
            }
        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create Fragment Navigator
        this.fragmentNavigator = new SparseArray<Fragment>();
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);



        //Refresh Login Attempts
        DrivesManager.getInstance().refreshLoginAttemps();


        //Drawer
        setupNavigationDrawer();


        //Button Floating
        fab = (FloatingActionButton) findViewById(R.id.addFile);
        fab.setOnClickListener(this);

        fabmenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        fabmenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            View v = findViewById(R.id.ivBlueScreen);
            @Override
            public void onMenuExpanded() {
                UiUtils.OpeningAnimate(v,Techniques.FadeIn,300);
            }

            @Override
            public void onMenuCollapsed() {
                UiUtils.ClosingAnimate(v,Techniques.FadeOut,300);
            }
        });



        if (!DriveUser.getInstance().isSignedIn()) {
            generalProgressDialog = UiUtils.getDefaultProgressDialog(this,true,getString(R.string.checking_login));
            generalProgressDialog.show();
            DrivesManager.getInstance().autoSignIn(this, signInCallback);
        }else{
            int availableDrive = DriveUser.getInstance().getAvailableDrive();
            if (availableDrive != -1)
                signInCallback.callback(EventConst.RELOGIN_SUCCESS,availableDrive,null);
            else
            {
                changeToNotConnected(0);
                //TODO: handle when there is no drive signed in
            }
        }





    }


    private void registerCallBack(){
        EventBroker.getInstance().register(this.signInCallback,EventConst.DISCONNECT);
        EventBroker.getInstance().register(this.signInCallback,EventConst.LOGIN_SUCCESS);
        EventBroker.getInstance().register(this.signInCallback,EventConst.LOGIN_FAIL);
    }

    private void unregisterCallback(){
        EventBroker.getInstance().unRegister(this.signInCallback,EventConst.DISCONNECT);
        EventBroker.getInstance().unRegister(this.signInCallback,EventConst.LOGIN_SUCCESS);
        EventBroker.getInstance().unRegister(this.signInCallback,EventConst.LOGIN_FAIL);
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
        folderPathFragment = new FolderPathFragment();
        fragmentNavigator.put(DriveType.GOOGLE,new FileListFragment.Builder()
                .setFragmentName(getString(R.string.g_drive))
                .setDriveType(DriveType.GOOGLE)
                .setFolder("root")
                .setViewMode(LIST)
                .build() );
        fragmentNavigator.put(DriveType.DROPBOX, new FileListFragment.Builder()
                .setFragmentName(getString(R.string.dbox))
                .setDriveType(DriveType.DROPBOX)
                .setFolder("")
                .setViewMode(LIST)
                .build());
        fragmentNavigator.put(DriveType.LOCAL, new FileListFragment.Builder()
                .setFragmentName(getString(R.string.local_storage))
                .setDriveType(DriveType.LOCAL)
                .setFolder("S-Cloud")
                .setViewMode(LIST)
                .build());
        notconnectedFragment = new NotConnectedCloudFragment();
        notconnectedFragment.setDrive(0);
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
            ff = (FileListFragment) getSupportFragmentManager().findFragmentByTag(ROOT_TAG);
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
            FileListFragment.ViewMode vm = topFragment.getVm();
            getSupportFragmentManager().popBackStackImmediate();
            if (countStack == 2)
                toggle.setDrawerIndicatorEnabled(true);
            backFolderHandle(vm);
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
        header = navigationView.getHeaderView(0);
        header.setOnClickListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.local:
            case R.id.gdrive:
            case R.id.dbox:
                getSupportActionBar().setTitle
                        (getString(ResourcesUtils.getInstance().getStringId(id)));
                if (isConnectedDrive(id))
                    changeDrive(id);
                else
                    changeToNotConnected(id);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeToNotConnected(int id) {
        notconnectedFragment.setDrive(id);
        this.changeFragment(R.id.fragmentHolder,notconnectedFragment,ROOT_TAG);
        topFragment = null;
        this.folderPathFragment.refreshWithFolder(getString(R.string.not_connected));
    }

    private boolean isConnectedDrive(int id) {
        return DriveUser.getInstance().isSignedIn(id);
    }

    private void changeDrive(int id) {
        FileListFragment fragment;
        fragment = (FileListFragment) fragmentNavigator.get(id);
        if (topFragment != null && fragment.getDriveType() == topFragment.getDriveType())
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
        topFragment = fragment;
        folderPathFragment.refreshWithFolder(getString(ResourcesUtils.getInstance().getStringId(id)));

        changeFragment(R.id.fragmentHolder,fragment, ROOT_TAG);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.hdProfile:
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivityForResult(intent, EventConst.PROFILE_OPEN);
                break;
            case R.id.addFile:
                showFileChooser();

                fabmenu.collapse();
                break;
            case R.id.addFolder:

                fabmenu.collapse();
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
                showHideFab(View.VISIBLE);
                toggle.setDrawerIndicatorEnabled(false);
                SDriveFolder sdfo = (SDriveFolder) data;
                folderPathFragment.addFolder(sdfo.getName());
                CloudDriveWrapper.getInstance(topFragment.getDriveType()).addNewListFileTask(sdfo.getId());
                //Create new fragment
                topFragment = new FileListFragment.Builder()
                                .setDriveType(topFragment
                                .getDriveType())
                                .setFragmentName(sdfo.getName())
                                .setViewMode(topFragment.getVm())
                                .build();
                this.addFragmentToStack(R.id.fragmentHolder,topFragment,FragmentStackName.FILES);
                this.callback(HomeActivity.START,1,null);
                topFragment.loadRefresh(this);
                getSupportActionBar().setTitle(topFragment.getFragmentName());
                break;
            case EventConst.BACK_FOLDER:
                int numback = code;
                for (int i = 0 ; i < numback; i ++){
                    onBackPressed();
                }
                break;
            case EventConst.SCROLL_DOWN:
                showHideFab(View.GONE);
                break;
            case EventConst.SCROLL_UP:
                showHideFab(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private void showHideFab(int visibility){
        if (fabmenu.getVisibility() != visibility) {
            if (visibility == View.GONE)
                UiUtils.ClosingAnimate(fabmenu, Techniques.SlideOutDown, 300);
            else if (visibility == View.VISIBLE)
                UiUtils.OpeningAnimate(fabmenu, Techniques.SlideInUp, 300);
        }
    }

    private void updateNavHeader() {
        TextView tvname = (TextView) header.findViewById(R.id.tvUserName);
        CircleImageView civAvatar = (CircleImageView) header.findViewById(R.id.imvAvatarUser);
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
                    sdf.setFolder(topFragment.getFolder());


                    if (path == null || path.length() == 0)
                    {
                        Toast.makeText(this, "Cannot access this file", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        //TODO: maybe sycned
                        DrivesManager.getInstance().transferDataTo(topFragment.getDriveType(),sdf,false);
                    }
                    Log.d("TAG", "File Path: " + path);
                }
                break;
            case EventConst.PROFILE_OPEN:
                if (resultCode == EventConst.LOGIN_SUCCESS_RESULT_CODE){
                    DrivesManager.getInstance().autoSignIn(this, signInCallback);
                }
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
            FileListFragment.ViewMode vm = topFragment.getVm();
            super.onBackPressed();
            backFolderHandle(vm);
        }
    }


    private void backFolderHandle(FileListFragment.ViewMode vm){
        this.callback(HomeActivity.FINISH,1,null);
        folderPathFragment.backFolderStepSize(1);
        topFragment = getTopFragment();
        if (topFragment.getVm() != vm)
            topFragment.changeViewMode(vm);
        getSupportActionBar().setTitle(topFragment.getFragmentName());
        CloudDriveWrapper.getInstance(topFragment.getDriveType()).popListFileTask();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
