package thesis.tg.com.s_cloud.user_interface.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import thesis.tg.com.s_cloud.framework_components.user_interface.adapter.KasperRecycleAdapter;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.user_interface.adapter.view_holder.FolderPathViewHolder;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by CKLD on 5/26/17.
 */

public class FolderPathAdapter extends KasperRecycleAdapter{
    public FolderPathAdapter(Context context, int resource, List entities) {
        super(context, resource, entities);
    }

    @Override
    protected RecyclerView.ViewHolder getItemViewHolder(View convertView, Context context) {
        return new FolderPathViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyCallBack)context).callback(EventConst.BACK_FOLDER,getItemCount()-position-1,entities.get(position));
            }
        });
    }

    public void removeFromLastAmount(int s) {
        for (int i = 0; i < s; i++){
            this.entities.remove(this.getItemCount()-1);
        }
    }
}
