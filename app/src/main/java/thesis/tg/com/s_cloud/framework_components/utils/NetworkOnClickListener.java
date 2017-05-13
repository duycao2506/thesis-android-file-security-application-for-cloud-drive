package thesis.tg.com.s_cloud.framework_components.utils;

import android.content.Context;
import android.view.View;

import thesis.tg.com.s_cloud.R;


/**
 * Created by admin on 4/2/17.
 */

public class NetworkOnClickListener implements View.OnClickListener, MyCallBack {
    Context context;
    public NetworkOnClickListener(Context context, MyCallBack caller){
        super();
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        GlobalUtils.getInstance().hasActiveInternetConnection(this.context, this);
    }



    @Override
    public void callback(String message, int code, Object data) {
        if (!(boolean) data){
            GlobalUtils.getInstance().displayDialog(context,context.getString(R.string.OK),"",message,null);
        }
    }
}
