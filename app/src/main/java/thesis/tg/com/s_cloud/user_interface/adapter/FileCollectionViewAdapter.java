package thesis.tg.com.s_cloud.user_interface.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;

import java.util.List;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.from_third_party.GoogleDownloadTask;
import thesis.tg.com.s_cloud.data.from_third_party.GoogleDriveWrapper;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.entities.SDriveFolder;
import thesis.tg.com.s_cloud.framework_components.entity.SuperObject;
import thesis.tg.com.s_cloud.framework_components.user_interface.adapter.KasperRecycleAdapter;
import thesis.tg.com.s_cloud.framework_components.user_interface.adapter.LoadingViewHolder;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.user_interface.adapter.view_holder.FileViewHolder;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by admin on 4/27/17.
 */

public class FileCollectionViewAdapter extends KasperRecycleAdapter{


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
                if (!(sdf instanceof SDriveFolder)) return;
                ((MyCallBack)context).callback(EventConst.OPEN_FOLDER,position
                        ,sdf);
            }
        });

        FileViewHolder viewHolder = (FileViewHolder) holder;
        viewHolder.bindData((SuperObject) this.entities.get(position));
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


}
