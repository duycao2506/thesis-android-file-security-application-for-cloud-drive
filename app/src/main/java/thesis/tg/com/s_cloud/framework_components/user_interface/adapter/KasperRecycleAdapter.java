package thesis.tg.com.s_cloud.framework_components.user_interface.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.framework_components.entity.SuperObject;

/**
 * Created by admin on 4/2/17.
 */

public class KasperRecycleAdapter extends RecyclerView.Adapter {

    protected final int VIEW_TYPE_ITEM = 0;
    protected final int VIEW_TYPE_LOADING = 1;


    protected Context context;
    protected int resource;
    protected boolean isLoadingmore = false;
    protected List entities;


    public KasperRecycleAdapter(Context context, int resource) {
        this.context = context;
        this.resource = resource;
    }

    public KasperRecycleAdapter(Context context, int resource, List entities) {
        this.context = context;
        this.resource = resource;
        this.entities = entities;
    }

    public boolean isLoadingmore() {
        return isLoadingmore;
    }

    public void setLoadingmore(boolean loadingmore) {
        isLoadingmore = loadingmore;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View convertView = LayoutInflater.from(context).inflate( resource , parent, false);
            return getItemViewHolder(convertView, context);
        }
        if (viewType == VIEW_TYPE_LOADING){
            View convertView = LayoutInflater.from(context).inflate( R.layout.view_holder_loading , parent, false);
            return new LoadingViewHolder(convertView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        EntityShower es = (EntityShower) holder;
        es.bindData(entities.get(position));
    }

    private int getListSize(){
        return entities.size();
    }

    @Override
    public int getItemCount() {
        return getListSize() + (isLoadingmore? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return position >= getListSize()? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public <T> void setEntities(List<T> entities) {
        this.entities = entities;
        this.notifyDataSetChanged();
    }

    public <T> void addEntities(List<T> entities){
        this.entities.addAll(entities);
        this.notifyDataSetChanged();
    }

    protected RecyclerView.ViewHolder getItemViewHolder(View convertView, Context context){
        return null;
    }


    public int getViewholder_res() {
        return resource;
    }

    public void setViewholder_res(int viewholder_res) {
        this.resource = viewholder_res;
    }
}
