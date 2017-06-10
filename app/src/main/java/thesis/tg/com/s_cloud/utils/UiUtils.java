package thesis.tg.com.s_cloud.utils;

import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;

import java.io.BufferedInputStream;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.DrivesManager;
import thesis.tg.com.s_cloud.entities.DriveUser;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;

import static thesis.tg.com.s_cloud.utils.EventConst.LOGIN_CANCEL_RESULT_CODE;

/**
 * Created by admin on 5/12/17.
 */

public class UiUtils {

    public static int TIME_ANIMATE_STANDARD = 800;


    public static ProgressDialog getDefaultProgressDialog(Context context, boolean cancellable, String string){
        ProgressDialog pd = new ProgressDialog(context,R.style.gray_accent_progressdialog);
        pd.setIndeterminate(true);
        pd.setMessage(string);
        pd.setCancelable(cancellable);
        return pd;
    }

    public static void OpeningAnimate(final View view, Techniques techniques, int time){
        YoYo.with(techniques).duration(time)
                .onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        view.setVisibility(View.VISIBLE);
                    }
                }).playOn(view);
    }
    public static void ClosingAnimate(final View view, Techniques techniques, int time){
        YoYo.with(techniques).duration(time)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        view.setVisibility(View.GONE);
                    }
                }).playOn(view);
    }

    public static void showExitAlertDialog(final Activity context, final int resultcode){
        AlertDialog.Builder ab = new AlertDialog.Builder(context, R.style.KasperAlertDialog);
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

        BottomSheetBuilder bsb = new BottomSheetBuilder(context, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .setTitleTextColorResource(R.color.gray_dark_transparent)
                .addTitleItem(R.string.wtdn)
                .setIconTintColorResource(R.color.gray_dark_transparent)
                .addItem(R.id.upload,R.string.upload, ContextCompat.getDrawable(context,R.drawable.ic_cloud_upload_black_24dp))
                .addItem(R.id.delete, R.string.delete,ContextCompat.getDrawable(context,R.drawable.ic_delete_black_24dp))
                .addItem(R.id.info, R.string.get_info, ContextCompat.getDrawable(context,R.drawable.ic_info_black_24dp));

        if (data.getCloud_type() == DriveType.LOCAL){
            bsb.addItem(R.id.open, R.string.open_with, ContextCompat.getDrawable(context,R.drawable.ic_open_with_black_24dp));
        }


        BottomSheetMenuDialog dialog = bsb.setItemClickListener(new BottomSheetItemClickListener() {
                    @Override
                    public void onBottomSheetItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.upload:
                                buildDriveMenu(data,context,false);
                                break;
                            case R.id.open:
                                break;
                            case R.id.info:
                                break;
                            case R.id.delete:
                                BaseApplication ba = (BaseApplication) context.getApplicationContext();
                                ba.getDriveWrapper(data.getCloud_type()).requestDelete(data.getId(), (MyCallBack) context);
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
        final BaseApplication ba = (BaseApplication) context.getApplicationContext();
        final ResourcesUtils resrcMnger = ba.getResourcesUtils();
        BottomSheetBuilder bsb = new BottomSheetBuilder(context, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .addTitleItem(isSync ? R.string.sync_with : R.string.upload)
                .setTitleTextColorResource(R.color.gray_dark_transparent)
                .setItemClickListener(new BottomSheetItemClickListener() {
                    @Override
                    public void onBottomSheetItemClick(MenuItem item) {
                        if (DriveUser.getInstance().isSignedIn(item.getItemId())){
                            ba.getDriveMannager().transferDataTo(item.getItemId(),data,isSync);
                        }else{
                            Toast.makeText(context
                                    , context.getString(R.string.not_connected_to)
                                            + resrcMnger.getStringId(item.getItemId())
                                    ,Toast.LENGTH_LONG).show();
                        }

                    }
                });

        for (int i = 0; i < ba.getDriveMannager().getNumDrive(); i++){
            if (data.getCloud_type()==resrcMnger.getTypeByIndex(i)) continue;
            int type = resrcMnger.getTypeByIndex(i);
            bsb = bsb.addItem(type,resrcMnger.getStringId(type), resrcMnger.getDriveIconId(type));
        }


        BottomSheetMenuDialog dialog = bsb.createDialog();
        dialog.setTitle(context.getString(R.string.which_drive));
        dialog.show();
    }
}
