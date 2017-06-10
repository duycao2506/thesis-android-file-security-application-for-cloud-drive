package thesis.tg.com.s_cloud.user_interface.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.framework_components.user_interface.fragment.KasperFragment;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by CKLD on 6/9/17.
 */

public class NameInputFragment extends DialogFragment implements View.OnClickListener{
    View parent;
    Button btnOk, btnCancel;
    EditText edtFoldername;
    MyCallBack caller;

    public NameInputFragment() {

    }

    public void setCaller(MyCallBack caller) {
        this.caller = caller;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parent = inflater.inflate(R.layout.fragment_inputname,container,false);
        edtFoldername = (EditText) parent.findViewById(R.id.edtFolderName);
        edtFoldername.setText(R.string.new_folder);
        btnCancel = (Button) parent.findViewById(R.id.btnCancelFolder);
        btnCancel.setOnClickListener(this);
        btnCancel.setActivated(true);
        btnOk = (Button) parent.findViewById(R.id.btnOkayFolder);
        btnOk.setOnClickListener(this);
        btnOk.setActivated(true);
        this.getDialog().setTitle(R.string.new_folder);
        return parent;
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
