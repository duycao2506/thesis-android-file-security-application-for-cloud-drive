package thesis.tg.com.s_cloud.framework_components;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.SparseArray;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.CloudDriveWrapper;
import thesis.tg.com.s_cloud.data.DrivesManager;
import thesis.tg.com.s_cloud.framework_components.data.from_server.VolleyHelper;
import thesis.tg.com.s_cloud.utils.DriveType;
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



    @Override
    public void onCreate() {
        super.onCreate();
        instance = VolleyHelper.getInstance(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        createDriveWrappers();
        this.driveMannager = DrivesManager.create(this);
        this.resourcesUtils = ResourcesUtils.getInstance(this);
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
}
