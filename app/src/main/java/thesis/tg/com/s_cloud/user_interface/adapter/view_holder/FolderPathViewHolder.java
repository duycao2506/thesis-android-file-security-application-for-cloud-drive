package thesis.tg.com.s_cloud.user_interface.adapter.view_holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.framework_components.entity.SuperObject;
import thesis.tg.com.s_cloud.framework_components.user_interface.adapter.EntityShower;

/**
 * Created by CKLD on 5/26/17.
 */

public class FolderPathViewHolder extends RecyclerView.ViewHolder implements EntityShower {
    TextView folderName;
    public FolderPathViewHolder(View itemView) {
        super(itemView);
        folderName = (TextView) itemView.findViewById(R.id.tvFolderName);
    }

    @Override
    public void bindData(Object so) {
        folderName.setText(so.toString());
    }
}
