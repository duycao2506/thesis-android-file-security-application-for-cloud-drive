package thesis.tg.com.s_cloud.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;

import thesis.tg.com.s_cloud.R;

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
}
