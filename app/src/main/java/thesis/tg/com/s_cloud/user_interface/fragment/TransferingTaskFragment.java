package thesis.tg.com.s_cloud.user_interface.fragment;

import android.view.View;

import java.util.List;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.from_third_party.task.TransferTask;
import thesis.tg.com.s_cloud.data.from_third_party.task.TransferTaskManager;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.user_interface.adapter.KasperRecycleAdapter;
import thesis.tg.com.s_cloud.framework_components.user_interface.fragment.RecycleViewFragment;
import thesis.tg.com.s_cloud.framework_components.utils.GlobalEventListennerDelegate;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.user_interface.adapter.TransferingTaskAdapter;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by CKLD on 6/16/17.
 */

public class TransferingTaskFragment extends RecycleViewFragment {

    String[] globalEvents = {EventConst.FINISH_DOWNLOADING, EventConst.FINISH_UPLOADING, EventConst.FAIL_TRANSFER};


    public TransferingTaskFragment() {

    }

    @Override
    protected void onKasperViewCreate(View parent) {
        super.onKasperViewCreate(parent);
        register(this.globalEvents,this);
    }

    @Override
    protected KasperRecycleAdapter initListViewAdapter() {
        BaseApplication ba = (BaseApplication) this.getContext().getApplicationContext();
        List<TransferTask> dataTask = ba.getTransferTaskManager().getTasks();
        if (dataTask.size() == 0)
            ((MyCallBack) getContext())
                .callback(
                        EventConst.NOTICE,
                        R.drawable.ic_close_black_24dp,
                        getString(R.string.nothing_toshow));
        else
            ((MyCallBack) getContext())
                    .callback(EventConst.HIDE_NOTICE,1,"");

        listViewAdapter = new TransferingTaskAdapter(dataTask, R.layout.view_holder_transfer_item,getContext());
        return listViewAdapter;
    }

    @Override
    protected void loadRefresh(MyCallBack caller) {
        super.loadRefresh(caller);
        listViewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void loadMore(MyCallBack caller) {
        super.loadMore(caller);
    }

    @Override
    protected void loadEntities(MyCallBack caller) {
        super.loadEntities(caller);
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        swipeLayout.setRefreshing(false);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unRegister(this.globalEvents,this);
    }

    @Override
    public void callback(String message, int code, Object data) {
        super.callback(message, code, data);
        switch (message){
            case EventConst.FINISH_DOWNLOADING:
            case EventConst.FINISH_UPLOADING:
            case EventConst.FAIL_TRANSFER:
                this.listViewAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }
}
