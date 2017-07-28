package thesis.tg.com.s_cloud.utils;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Xml;

import com.google.common.base.Charsets;
import com.google.common.primitives.Doubles;

import java.net.NetworkInterface;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;

/**
 * Created by admin on 4/26/17.
 */

public class DataUtils {

    public static byte[] getDataHeader(){
        byte[] bytes = new byte[4];
        bytes[0] = 1;
        bytes[1] = 2;
        bytes[2] = 6;
        bytes[3] = 0;
        return  (bytes);
    }

    public static String encodePassword(String key, String lock){
        byte[] keyBytes = key.getBytes(Charsets.UTF_8);
        byte[] lockBytes = lock.getBytes(Charsets.UTF_8);
        for (int i = 0; i < lockBytes.length; i++){
            lockBytes[i] = (byte) (lockBytes[i] ^ keyBytes[i%keyBytes.length]);
        }
        return new String(lockBytes,Charsets.UTF_8);
    }




    public static void waitFor(final long mls, final Context context, final MyCallBack handler){
        new AsyncTask<Void, Void, Void>(){
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(context);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
            }


            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(mls);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressDialog.dismiss();
                handler.callback("Stop waiting",2,"");
            }
        }.execute();
    }

    public static String getPath(final Context context, final Uri uri)
    {

        //check here to KITKAT or new version

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                String path = cursor.getString(index);
                return path;
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String fileSizeToString(long fileSize) {
        double oneotwofour = 1024;
        DecimalFormat df2 = new DecimalFormat(".##");
        String[] units = {"Bytes","KB","MB","GB"};
        double s = fileSize;
        int i = 0;
        for (; i < 4 && s > 1024;i++){
            s =  s*1.0/oneotwofour;

        }
        return df2.format(s) + " " + units[i];

    }

    public static void sendEmailAutomatically(final String to, final String from, final String content, final String subject, final MyCallBack caller){
        new AsyncTask<Object, Object, Boolean>(){
            @Override
            protected Boolean doInBackground(Object... params) {
                try {
                    GMailSender sender = new GMailSender("rogernorman2506@gmail.com", "25061995");
                    sender.sendMail(subject,
                            content,
                            from,
                            to);
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);
                if (aVoid)
                    caller.callback(EventConst.SEND_EMAIL, 1, "");
                else
                    caller.callback(EventConst.SEND_EMAIL,0,"");
            }
        }.execute();
    }

    public static String getMacAddress(String interfaceName,Context context){
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
        String macaddr = sp.getString("macAddr","");




        if (macaddr.length() == 0) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                try {
                    Context cntxt = context.getApplicationContext();
                    WifiManager wifi = (WifiManager) cntxt.getSystemService(Context.WIFI_SERVICE);
                    if (wifi == null) {
                        macaddr = "";
                    }else {
                        WifiInfo info = wifi.getConnectionInfo();
                        if (info == null) {
                            macaddr = "";
                        }else
                            macaddr = info.getMacAddress();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                    for (NetworkInterface intf : interfaces) {
                        if (interfaceName != null) {
                            if (!intf.getName().equalsIgnoreCase(interfaceName))
                                continue;
                        }
                        byte[] mac = intf.getHardwareAddress();


                        if (mac == null) {
                            macaddr = "";
                        }else {
                            StringBuilder buf = new StringBuilder();
                            for (int idx = 0; idx < mac.length; idx++)
                                buf.append(String.format("%02X:", mac[idx]));
                            if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                            macaddr = buf.toString();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }//else{
//            DeviceAdminReceiver admin = new DeviceAdminReceiver();
//            DevicePolicyManager devicepolicymanager = admin.getManager(context.getApplicationContext());
//            ComponentName name1 = admin.getWho(context.getApplicationContext());
//            String mac_address = devicepolicymanager.getWifiMacAddress(name1);
//            caller.calback(EventConst.MAC_ADDR, 1, mac_address);
//            return;
//        }

        if (macaddr.length() > 0){
            spe.putString("macAddr",macaddr);
            spe.apply();
        }
        Log.e("MAC", macaddr);
        return macaddr;
    }


}

