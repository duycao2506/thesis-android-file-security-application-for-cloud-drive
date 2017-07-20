package thesis.tg.com.s_cloud.framework_components;

import android.content.Context;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.SparseArray;

import java.io.UnsupportedEncodingException;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.CloudDriveWrapper;
import thesis.tg.com.s_cloud.data.DrivesManager;
import thesis.tg.com.s_cloud.data.from_third_party.google_drive.GoogleDriveWrapper;
import thesis.tg.com.s_cloud.data.from_third_party.task.TransferTaskManager;
import thesis.tg.com.s_cloud.entities.DriveUser;
import thesis.tg.com.s_cloud.framework_components.data.from_server.VolleyHelper;
import thesis.tg.com.s_cloud.framework_components.utils.EventBroker;
import thesis.tg.com.s_cloud.security.SimpleRSACipher;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.NotificationHandler;
import thesis.tg.com.s_cloud.utils.ResourcesUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


/**
 * Created by admin on 4/2/17.
 */

public class BaseApplication extends MultiDexApplication {
    private VolleyHelper instance;
    private SparseArray<CloudDriveWrapper> cdw;
    private DrivesManager driveMannager;
    private ResourcesUtils resourcesUtils;
    private TransferTaskManager transferTaskManager;
    private EventBroker eventBroker;
    private NotificationHandler notificationHandler;
    private DriveUser driveUser;
    private SimpleRSACipher simpleCipher;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = VolleyHelper.getInstance(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        createDriveWrappers();
        this.driveUser = DriveUser.getInstance(this);
        this.driveMannager = DrivesManager.create(this);
        this.resourcesUtils = ResourcesUtils.getInstance(this);
        this.transferTaskManager = TransferTaskManager.getInstance(this);
        this.eventBroker = EventBroker.getInstance(this);
        notificationHandler = new NotificationHandler(this);
    }

    private void createDriveWrappers() {
        cdw = new SparseArray<CloudDriveWrapper>();
        cdw.put(DriveType.GOOGLE, CloudDriveWrapper.create(DriveType.GOOGLE, this));
        cdw.put(DriveType.DROPBOX, CloudDriveWrapper.create(DriveType.DROPBOX, this));
        cdw.put(DriveType.LOCAL, CloudDriveWrapper.create(DriveType.LOCAL, this));
    }


    public VolleyHelper getVolleyInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    public boolean isDriveWrapperCreated(int type){
        return cdw.get(type) != null;
    }

    public CloudDriveWrapper getDriveWrapper(int type) {
        return cdw.get(type);
    }

    public DrivesManager getDriveMannager() {
        return driveMannager;
    }

    public ResourcesUtils getResourcesUtils() {
        return resourcesUtils;
    }

    public TransferTaskManager getTransferTaskManager() {
        return transferTaskManager;
    }

    public EventBroker getEventBroker() {
        return eventBroker;
    }

    public DriveUser getDriveUser() {
        return driveUser;
    }

    public void setDriveUser(DriveUser driveUser) {
        this.driveUser = driveUser;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ((GoogleDriveWrapper)getDriveWrapper(DriveType.GOOGLE)).getClient().disconnect();
    }

    public SimpleRSACipher getSimpleCipher(String mac_addr) throws UnsupportedEncodingException {
        if (simpleCipher == null) {
            this.simpleCipher = new SimpleRSACipher(mac_addr, Build.BRAND, Build.MODEL);
        }
        return simpleCipher;
    }

    public SimpleRSACipher getSimpleCipher(){
        return simpleCipher;
    }

    public void setSimpleCipher(SimpleRSACipher simpleCipher) {
        this.simpleCipher = simpleCipher;
    }


}
