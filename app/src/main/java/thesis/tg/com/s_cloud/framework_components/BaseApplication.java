package thesis.tg.com.s_cloud.framework_components;

import android.app.Application;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.framework_components.data.from_server.VolleyHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


/**
 * Created by admin on 4/2/17.
 */

public class BaseApplication extends Application {
    private static VolleyHelper instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = VolleyHelper.getInstance(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    public static VolleyHelper getVolleyInstance() {
        return instance;
    }


}
