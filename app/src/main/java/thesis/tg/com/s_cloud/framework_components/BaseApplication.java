package thesis.tg.com.s_cloud.framework_components;

import android.app.Application;
import android.content.Context;

import thesis.tg.com.s_cloud.framework_components.data.from_server.VolleyHelper;


/**
 * Created by admin on 4/2/17.
 */

public class BaseApplication extends Application {
    private static VolleyHelper instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = VolleyHelper.getInstance(this);
    }

    public static VolleyHelper getVolleyInstance() {
        return instance;
    }

}
