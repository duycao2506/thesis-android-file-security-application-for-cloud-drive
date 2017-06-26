package thesis.tg.com.s_cloud.user_interface.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.entities.SDriveFolder;
import thesis.tg.com.s_cloud.framework_components.entity.SuperObject;
import thesis.tg.com.s_cloud.framework_components.user_interface.adapter.KasperRecycleAdapter;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.user_interface.adapter.view_holder.FileViewHolder;
import thesis.tg.com.s_cloud.user_interface.fragment.FileListFragment;
import thesis.tg.com.s_cloud.utils.EventConst;
import thesis.tg.com.s_cloud.utils.UiUtils;

import static android.R.attr.filter;

/**
 * Created by admin on 4/27/17.
 */

public class FileCollectionViewAdapter extends KasperRecycleAdapter{


    private List<SDriveFile> filterList;
    private FileListFragment.FileListFilter filter;

    public FileCollectionViewAdapter(List<SDriveFile> superObjects, int viewholder_res, Context context) {
        super(context, viewholder_res, superObjects);
    }

    @Override
    protected RecyclerView.ViewHolder getItemViewHolder(View convertView, Context context) {
        FileViewHolder fileViewHolder = new FileViewHolder(getViewholder_res(), convertView,this.context);
        return fileViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (position >= this.entities.size()) return;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Open folder or open file
                SDriveFile sdf = (SDriveFile) entities.get(position);
                if (!(sdf instanceof SDriveFolder)) {
                    UiUtils.openInfo(sdf,context);
                    return;
                }
                ((MyCallBack)context).callback(EventConst.OPEN_FOLDER,position
                        ,sdf);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SDriveFile sdf = (SDriveFile) entities.get(position);
                if (sdf instanceof SDriveFolder)
                    UiUtils.showDeleteFileConfirmation((Activity) context,sdf);
                else
                    UiUtils.buildFileMenu(sdf,context);
                return true;
            }
        });

        FileViewHolder viewHolder = (FileViewHolder) holder;
        viewHolder.bindData( this.entities.get(position));
    }

    @Override
    public <T> void addEntities(List<T> entities) {
        for (T i : entities){
            if (i instanceof SDriveFolder)
                this.entities.add(0,i);
            else
                this.entities.add(i);
        }
    }



    public void filterList(String text){
        filter.filter(text);
    }

    @Override
    public <T> void setEntities(List<T> entities) {
        super.setEntities(entities);
        if (filter != null)
            filter.setBaseList(this.entities);
    }

    public  void setFilterList(List filterList){
        this.entities = filterList;
    }

    public void initFilter (FileListFragment.FileListFilter filter){
        this.filter = filter;
    }
}
