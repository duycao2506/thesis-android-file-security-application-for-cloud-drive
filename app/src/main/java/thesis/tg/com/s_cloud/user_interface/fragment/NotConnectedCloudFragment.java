package thesis.tg.com.s_cloud.user_interface.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.framework_components.user_interface.fragment.KasperFragment;
import thesis.tg.com.s_cloud.user_interface.activity.ProfileActivity;
import thesis.tg.com.s_cloud.utils.EventConst;
import thesis.tg.com.s_cloud.utils.ResourcesUtils;

/**
 * Created by CKLD on 5/30/17.
 */

public class NotConnectedCloudFragment extends KasperFragment {
    ImageView ivDriveIcon;
    Button btnFix;
    int driveType = 0;

    public NotConnectedCloudFragment() {
        super();
        this.setResource(R.layout.fragment_cloud_n_connected);
    }

    @Override
    protected void onKasperViewCreate(View parent) {
        super.onKasperViewCreate(parent);
        ivDriveIcon = (ImageView) parent.findViewById(R.id.ivNconnectedDrico);
        if (driveType == 0) {
            if (ivDriveIcon == null) return;
            ivDriveIcon.setImageResource(R.drawable.ic_cloud_upload_black_24dp);
            return;
        }else {
            ivDriveIcon.setImageResource
                    (ba.getResourcesUtils().getDriveIconId(driveType));
        }
        btnFix = (Button) parent.findViewById(R.id.btnFixDriveConn);
        btnFix.setActivated(true);
        btnFix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                getActivity().startActivityForResult(intent,EventConst.PROFILE_OPEN);
            }
        });
    }

    public void setDrive(int driveType){
        this.driveType = driveType;
        if (isVisible()){
            if (driveType == 0) {
                if (ivDriveIcon == null) return;
                ivDriveIcon.setImageResource(R.drawable.ic_cloud_upload_black_24dp);
                return;
            }else
                ivDriveIcon.setImageResource
                        (ba.getResourcesUtils().getDriveIconId(driveType));
        }
    }
}
