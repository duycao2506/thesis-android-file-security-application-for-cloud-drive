package thesis.tg.com.s_cloud.user_interface.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.framework_components.user_interface.fragment.KasperFragment;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by CKLD on 6/9/17.
 */

public class NameInputFragment extends BottomSheetDialogFragment implements View.OnClickListener{
    View parent;
    ImageView btnOk, btnCancel;
    EditText edtFoldername;
    MyCallBack caller;

    public NameInputFragment() {

    }

    public void setCaller(MyCallBack caller) {
        this.caller = caller;
    }


    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        parent = View.inflate(getContext(),R.layout.fragment_inputname,null);
        dialog.setContentView(parent);
        edtFoldername = (EditText) parent.findViewById(R.id.edtFolderName);
        edtFoldername.setText(R.string.new_folder);

        edtFoldername.setFocusableInTouchMode(true);
        edtFoldername.requestFocus();

        btnCancel = (ImageView) parent.findViewById(R.id.btnCancelFolder);
        btnCancel.setOnClickListener(this);
        btnCancel.setActivated(true);
        btnOk = (ImageView) parent.findViewById(R.id.btnOkayFolder);
        btnOk.setOnClickListener(this);
        btnOk.setActivated(true);
        this.getDialog().getContext().setTheme(android.R.style.Theme_Material_Light_Dialog_NoActionBar);
//        this.getDialog().getActionBar().setIcon(R.drawable.ic_folder_white_24dp);
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnCancelFolder:
                caller.callback(EventConst.INPUT_FOLDER_NAME_FIN, EventConst.FAIL,"");
                dismiss();
                break;
            case R.id.btnOkayFolder:
                String foldername = edtFoldername.getText().toString();
                if (foldername.length() == 0){
                    this.btnCancel.callOnClick();
                    return;
                }
                caller.callback(EventConst.INPUT_FOLDER_NAME_FIN, EventConst.SUCCESS,edtFoldername.getText());
                dismiss();
                break;

        }

    }
}
