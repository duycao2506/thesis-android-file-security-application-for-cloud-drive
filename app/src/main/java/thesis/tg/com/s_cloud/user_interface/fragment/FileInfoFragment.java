package thesis.tg.com.s_cloud.user_interface.fragment;

import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DataUtils;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by CKLD on 6/25/17.
 */

public class FileInfoFragment extends BottomSheetDialogFragment {

    View parent;

    MyCallBack caller;
    SDriveFile data;

    TextView tvFilename, tvFilesize, tvFiletype, tvFileLastModified;

    public FileInfoFragment() {

    }

    public SDriveFile getData() {
        return data;
    }

    public void setData(SDriveFile data) {
        this.data = data;
    }

    public void setCaller(MyCallBack caller) {
        this.caller = caller;
    }


    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        parent = View.inflate(getContext(), R.layout.fragment_dialog_file_info,null);
        dialog.setContentView(parent);

        assignViews(parent);

        pourDataOnViews();

        this.getDialog().getContext().setTheme(android.R.style.Theme_Material_Light_Dialog_NoActionBar);
//        this.getDialog().getActionBar().setIcon(R.drawable.ic_folder_white_24dp);
    }

    private void pourDataOnViews() {
        this.tvFilename.setText(data.getName());
        String mime = data.getMimeType();
        this.tvFiletype.setText(mime == null || mime.length() == 0? getString(R.string.unknown) : mime);
        this.tvFileLastModified.setText(data.getLastModifiedDate());
        this.tvFilesize.setText(
                data.getFileSize() == -1 ? getString(R.string.unavail) : DataUtils.fileSizeToString(data.getFileSize()));
    }

    private void assignViews(View parent) {
        this.tvFileLastModified = (TextView) parent.findViewById(R.id.tvFileLastModified);
        this.tvFilename = (TextView) parent.findViewById(R.id.tvFileName);
        this.tvFilesize = (TextView) parent.findViewById(R.id.tvFilesize);
        this.tvFiletype = (TextView) parent.findViewById(R.id.tvFileType);
    }
}
