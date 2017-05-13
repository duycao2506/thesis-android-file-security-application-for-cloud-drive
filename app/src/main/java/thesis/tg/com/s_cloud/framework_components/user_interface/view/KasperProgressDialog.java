package thesis.tg.com.s_cloud.framework_components.user_interface.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import thesis.tg.com.s_cloud.R;


/**
 * Created by admin on 4/2/17.
 */

public class KasperProgressDialog extends ProgressDialog {
    private AnimationDrawable animation;
    private int contentView;

    public static ProgressDialog ctor(Context context, int theme, int contentView) {
        KasperProgressDialog dialog = new KasperProgressDialog(context, theme);
        dialog.contentView = contentView;
        dialog.getWindow().setWindowAnimations(R.style.progress_animation_in_out);
        dialog.getWindow().setDimAmount(2.0f);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }


    public KasperProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public void setAnimation(Drawable drawable){
        animation = (AnimationDrawable) drawable;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(contentView);

    }

    @Override
    public void show() {
        super.show();
        if (animation!=null)
            animation.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (animation!=null)
            animation.stop();
    }
}
