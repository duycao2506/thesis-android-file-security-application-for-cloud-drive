package thesis.tg.com.s_cloud.user_interface.adapter.view_holder;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.DrivesManager;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.entities.SDriveFolder;
import thesis.tg.com.s_cloud.framework_components.entity.SuperObject;
import thesis.tg.com.s_cloud.framework_components.user_interface.adapter.EntityShower;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.UiUtils;

/**
 * Created by admin on 4/27/17.
 */

public class FileViewHolder extends RecyclerView.ViewHolder implements EntityShower {
    ImageView imgMain, imgSecured, imgDrive1, imgDrive2, imgDrive3;
    ImageView thumbnail;
    TextView name, date;
    ImageButton btnMenu;
    int res;
    Context context;

    public FileViewHolder(int resource, View itemView, Context context) {
        super(itemView);
        this.context = context;
        res = resource;
        imgMain = (ImageView) itemView.findViewById(R.id.ivMain);
        name = (TextView) itemView.findViewById(R.id.tvFileTitle);
        date = (TextView) itemView.findViewById(R.id.tvInfoFile);
        btnMenu = (ImageButton) itemView.findViewById(R.id.btnFileMenu);
        thumbnail = (ImageView) itemView.findViewById(R.id.ivThumbnail);

    }

    @Override
    public void bindData(final SuperObject so) {

        boolean isFolder = so instanceof SDriveFolder;
        imgMain.setImageResource(isFolder ? R.drawable.ic_folder_white_24dp : R.drawable.ic_insert_drive_file_white_24dp);
        imgMain.setBackgroundColor(
                Color.parseColor(
                        context
                                .getString(
                                        isFolder ?
                                                R.string.divider
                                                : R.string.gray_light)));
        final SDriveFile sdf = (SDriveFile) so;

        name.setText(sdf.getName());
        if (res == R.layout.view_holder_list_file) {
            date.setText(sdf.getCreatedDate());
        }

        if (res == R.layout.view_holder_grid_file) {
            thumbnail.setBackgroundColor(
                    Color.parseColor(

                            context
                                    .getString(
                                            isFolder ?
                                                    R.string.divider
                                                    : R.string.gray_light)));
        }

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.buildFileMenu(sdf, context);
            }
        });

    }



}
