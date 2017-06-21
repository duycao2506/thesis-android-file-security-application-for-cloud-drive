package thesis.tg.com.s_cloud.user_interface.adapter.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.from_third_party.task.TransferTask;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.user_interface.adapter.EntityShower;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by CKLD on 6/16/17.
 */

public class TransferTaskViewHolder extends RecyclerView.ViewHolder implements EntityShower {

    ImageView ivType;
    TextView from, to;
    TextView name;
    ProgressBar pb;

    public TransferTaskViewHolder(View itemView) {
        super(itemView);
        ivType = (ImageView) itemView.findViewById(R.id.ivTransferType);
        from = (TextView) itemView.findViewById(R.id.tvFromDrive);
        to = (TextView) itemView.findViewById(R.id.tvToDrive);
        name = (TextView) itemView.findViewById(R.id.tvFileNameTransfer);
        pb = (ProgressBar) itemView.findViewById(R.id.pbFileTask);
    }

    @Override
    public void bindData(Object so) {
        BaseApplication ba = (BaseApplication) itemView.getContext().getApplicationContext();
        TransferTask tt = (TransferTask) so;
        name.setText(tt.getFile().getName());
        from.setText(ba.getString(ba.getResourcesUtils().getStringId(tt.getFrom())));
        to.setText(ba.getString(ba.getResourcesUtils().getStringId(tt.getTo())));
        if (tt.getTo() == DriveType.LOCAL || tt.getTo() == DriveType.LOCAL_STORAGE)
            ivType.setImageResource(R.drawable.clouddown);
        else if (tt.getFrom() == DriveType.LOCAL || tt.getFrom() == DriveType.LOCAL_STORAGE)
            ivType.setImageResource(R.drawable.cloudup);
        else
            ivType.setImageResource(R.drawable.cloudtocloud);
        pb.setProgress(tt.getProgressPercent());
    }
}
