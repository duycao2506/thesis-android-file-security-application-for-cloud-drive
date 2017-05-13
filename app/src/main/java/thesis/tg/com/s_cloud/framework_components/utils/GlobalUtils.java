package thesis.tg.com.s_cloud.framework_components.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import thesis.tg.com.s_cloud.R;


/**
 * Created by admin on 4/2/17.
 */

public class GlobalUtils {

    private GlobalUtils(){

    }

    private static GlobalUtils instance;

    public static GlobalUtils getInstance(){
        if (instance == null)
            instance = new GlobalUtils();
        return instance;
    }

    public boolean isNetworkConnected(Context context){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public void hasActiveInternetConnection(final Context context, final MyCallBack caller) {
        if (isNetworkConnected(context)) {
            new AsyncTask<Object, Object, Integer>(){

                @Override
                protected Integer doInBackground(Object... params) {
                    try {
                        HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                        urlc.setRequestProperty("User-Agent", "Test");
                        urlc.setRequestProperty("Connection", "close");
                        urlc.setConnectTimeout(1500);
                        urlc.connect();
                        return urlc.getResponseCode();

                    } catch (IOException e) {

                        return -1;
                    }
                }

                @Override
                protected void onPostExecute(Integer integer) {
                    super.onPostExecute(integer);
                    if (integer == -1){
                        caller.callback(context.getString(R.string.error_check_internet),integer,false);
                        return;
                    }
                    if (integer != 200) {
                        caller.callback(context.getString(R.string.cant_internet), integer, false);
                        return;
                    }
                    caller.callback(context.getString(R.string.OK),integer,true);
                }
            }.execute();
        } else {
            caller.callback(context.getString(R.string.not_connect_internet),-1,false);
        }
    }

    public void displayDialog(Context context, String OK, String Cancel, String mess, final MyCallBack caller){
        AlertDialog.Builder ad = new AlertDialog.Builder(context)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        if (caller != null)
                            caller.callback("",0,true);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);
        if (Cancel.length() > 0){
            ad.setNegativeButton(Cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (caller != null)
                        caller.callback("",0,false);
                }
            });
        }
        ad.show();
    }
}
