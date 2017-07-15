package thesis.tg.com.s_cloud.user_interface.fragment;

import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by CKLD on 6/25/17.
 */

public class PasswordSetFragment extends BottomSheetDialogFragment implements View.OnClickListener{
    View parent;
    ImageView btnOk, btnCancel;

    int drivetype;

    public int getDrivetype() {
        return drivetype;
    }

    public void setDrivetype(int drivetype) {
        this.drivetype = drivetype;
    }

    EditText edtConfPass, edtPass;
    MyCallBack caller;

    public PasswordSetFragment() {

    }

    public void setCaller(MyCallBack caller) {
        this.caller = caller;
    }


    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        parent = View.inflate(getContext(), R.layout.fragment_dialog_password,null);
        dialog.setContentView(parent);
        edtPass = (EditText) parent.findViewById(R.id.edtPasswordReg);
        edtConfPass = (EditText) parent.findViewById(R.id.edtPasswordCnf);




        btnCancel = (ImageView) parent.findViewById(R.id.btnCancelRegister);
        btnCancel.setOnClickListener(this);
        btnCancel.setActivated(true);
        btnOk = (ImageView) parent.findViewById(R.id.btnOkRegister);
        btnOk.setOnClickListener(this);
        btnOk.setActivated(true);
        this.getDialog().getContext().setTheme(android.R.style.Theme_Material_Light_Dialog_NoActionBar);
//        this.getDialog().getActionBar().setIcon(R.drawable.ic_folder_white_24dp);
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnCancelRegister:
                caller.callback(
                        drivetype == DriveType.DROPBOX?
                                EventConst.CANCEL_SIGNUP_DROPBOX : EventConst.CANCEL_SIGNUP_GOOGLE,
                            EventConst.FAIL,
                            "");
                dismiss();
                break;
            case R.id.btnOkRegister:
                String confpass = edtConfPass.getText().toString();
                String pass = edtPass.getText().toString();
                if (pass.length() < 6 || confpass.length() < 6){
                    Toast.makeText(this.getContext(), R.string.more6,Toast.LENGTH_LONG).show();
                    return;
                }
                if (pass.compareTo(confpass) != 0){
                    Toast.makeText(this.getContext(), getString(R.string.confirm_wrong),Toast.LENGTH_LONG).show();
                    return;
                }
                caller.callback(EventConst.SET_PASSWORD, EventConst.SUCCESS, pass);
                dismiss();
                break;

        }

    }


}
