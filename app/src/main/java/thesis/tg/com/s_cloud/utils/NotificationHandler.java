package thesis.tg.com.s_cloud.utils;

import android.content.Intent;
import android.text.TextUtils;

import java.security.Timestamp;
import java.util.Date;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.utils.EventBroker;
import thesis.tg.com.s_cloud.framework_components.utils.GlobalEventListennerDelegate;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.user_interface.activity.HomeActivity;

/**
 * Created by CKLD on 6/21/17.
 */

public class NotificationHandler implements GlobalEventListennerDelegate, MyCallBack {
    String[] globalEvents = {EventConst.FINISH_DOWNLOADING, EventConst.FINISH_UPLOADING, EventConst.FAIL_TRANSFER};
    BaseApplication ba;

    public NotificationHandler(BaseApplication ba) {
        this.ba = ba;
        register(globalEvents, this);
    }


    @Override
    public void register(String[] events, MyCallBack caller) {
        for (String i : events) {
            EventBroker.getInstance(ba).register(caller, i);
        }

    }

    @Override
    public void unRegister(String[] events, MyCallBack caller) {
        for (String i : events) {
            EventBroker.getInstance(ba).unRegister(caller, i);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.unRegister(globalEvents, this);
    }

    @Override
    public void callback(String message, int code, Object data) {
        if (!NotificationUtils.isAppIsInBackground(ba)) return;
        String messageSent = "";
        switch (message) {
            case EventConst.FINISH_DOWNLOADING:
                messageSent = ba.getString(R.string.finish_downfile)+ " "  + ((SDriveFile)data).getName();
                break;
            case EventConst.FINISH_UPLOADING:
                messageSent = ba.getString(R.string.finish_upfile)+ " "  + ((SDriveFile)data).getName();
                break;
            case EventConst.FAIL_TRANSFER:
                messageSent = ba.getString(R.string.transfer_file)
                        + ((SDriveFile)data).getName()
                        + " "
                        + ba.getString(R.string.unsuccessfully);
                break;
            default:
                return;
        }

        Intent resultIntent = new Intent(ba, HomeActivity.class);
        resultIntent.putExtra("message", message);

        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        NotificationUtils notificationUtils = new NotificationUtils(ba);
        // check for image attachment
        notificationUtils.showNotificationMessage(ba.getString(R.string.app_name), messageSent, ts, resultIntent);

    }
}
