package thesis.tg.com.s_cloud.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;

import java.io.BufferedInputStream;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.DrivesManager;
import thesis.tg.com.s_cloud.entities.SDriveFile;

import static thesis.tg.com.s_cloud.utils.EventConst.LOGIN_CANCEL_RESULT_CODE;

/**
 * Created by admin on 5/12/17.
 */

public class UiUtils {

    public static void showExitAlertDialog(final Activity context, final int resultcode){
        AlertDialog.Builder ab = new AlertDialog.Builder(context);
        ab.setTitle(R.string.exit_app_tit);
        ab.setMessage(R.string.ext_app_mess);
        ab.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.setResult(resultcode);
                context.finish();
            }
        });
        ab.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ab.show();
    }


    public static void buildFileMenu(final SDriveFile data, final Context context) {

        BottomSheetMenuDialog dialog = new BottomSheetBuilder(context, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .setTitleTextColorResource(R.color.gray_dark_transparent)
                .addTitleItem(R.string.wtdn)
                .setIconTintColorResource(R.color.gray_dark_transparent)
                .addItem(R.id.upload,R.string.upload, ContextCompat.getDrawable(context,R.drawable.ic_cloud_upload_black_24dp))
                .addItem(R.id.sync,R.string.sync_with,ContextCompat.getDrawable(context,R.drawable.ic_sync_black_24dp))

                .setItemClickListener(new BottomSheetItemClickListener() {
                    @Override
                    public void onBottomSheetItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.upload:
                                buildDriveMenu(data,context,false);
                                break;
                            case R.id.sync:
                                buildDriveMenu(data,context,true);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .createDialog();
        dialog.setTitle(R.string.file_menu);
        dialog.show();
    }

    public static void buildDriveMenu(final SDriveFile data, final Context context, final boolean isSync) {
        BottomSheetMenuDialog dialog = new BottomSheetBuilder(context, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .addTitleItem(isSync ? R.string.sync_with : R.string.upload)
                .setTitleTextColorResource(R.color.gray_dark_transparent)
                .addItem(R.id.gdrive,R.string.g_drive,R.drawable.gdrive)
                .addItem(R.id.dbox,R.string.dbox,R.drawable.dbox)
                .addItem(R.id.local,R.string.local_storage,R.drawable.localstorage)
                .setItemClickListener(new BottomSheetItemClickListener() {
                    @Override
                    public void onBottomSheetItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.gdrive:
                                Toast.makeText(context,R.string.g_drive,Toast.LENGTH_SHORT).show();
                                DrivesManager.getInstance().transferDataTo(DriveType.GOOGLE,data,isSync);
                                break;
                            case R.id.dbox:
                                Toast.makeText(context,R.string.dbox,Toast.LENGTH_SHORT).show();
                                DrivesManager.getInstance().transferDataTo(DriveType.DROPBOX,data,isSync);
                                break;
                            case R.id.local:
                                DrivesManager.getInstance().transferDataTo(DriveType.LOCAL,data,isSync);
                                Toast.makeText(context,R.string.local_storage,Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                    }
                })
                .createDialog();
        dialog.setTitle(context.getString(R.string.which_drive));
        dialog.show();
    }
}
