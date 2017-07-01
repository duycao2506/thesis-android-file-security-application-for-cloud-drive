package thesis.tg.com.s_cloud.user_interface.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;
import java.util.concurrent.RunnableFuture;

import thesis.tg.com.s_cloud.data.from_third_party.task.TransferTask;
import thesis.tg.com.s_cloud.framework_components.user_interface.adapter.EntityShower;
import thesis.tg.com.s_cloud.framework_components.user_interface.adapter.KasperRecycleAdapter;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.user_interface.adapter.view_holder.TransferTaskViewHolder;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by CKLD on 6/16/17.
 */

public class TransferingTaskAdapter extends KasperRecycleAdapter implements MyCallBack {
    public TransferingTaskAdapter(List<TransferTask> tasks, int view_holder_transfer_item, Context context) {
        super(context,view_holder_transfer_item,tasks);
    }

    @Override
    protected RecyclerView.ViewHolder getItemViewHolder(View convertView, Context context) {
        return new TransferTaskViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        TransferTask tt = (TransferTask) this.entities.get(position);
        tt.setCaller(this);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        int pos = holder.getAdapterPosition();
        if (pos >= this.entities.size() || pos < 0) return;
    }

    public void removeCaller(){
        for (int i = 0; i < entities.size(); i++){
            ((TransferTask)entities.get(i)).setCaller(null);
        }
    }

    @Override
    public void callback(String message, int code, final Object data) {
        switch (message){
            case EventConst.PROGRESS_UPDATE:
                int pos = this.findObj(data);
                if (pos == -1)
                    break;
                notifyItemChanged(findObj(data));
                break;
            default:
                break;
        }
    }

    private int findObj(Object data) {
        for (int i = 0; i < this.entities.size(); i++){
            if (data == entities.get(i))
                return i;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_ITEM;
    }
}
