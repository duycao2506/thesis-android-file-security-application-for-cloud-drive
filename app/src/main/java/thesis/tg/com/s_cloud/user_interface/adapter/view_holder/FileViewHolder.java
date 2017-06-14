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
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.entity.SuperObject;
import thesis.tg.com.s_cloud.framework_components.user_interface.adapter.EntityShower;
import thesis.tg.com.s_cloud.utils.DataUtils;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.ResourcesUtils;
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
        imgDrive1 = (ImageView) itemView.findViewById(R.id.ivLocalDrive);
        imgDrive2 = (ImageView) itemView.findViewById(R.id.ivGoogleDrive);
        imgDrive3 = (ImageView) itemView.findViewById(R.id.ivDrive3);

    }

    @Override
    public void bindData(final Object so) {

        boolean isFolder = so instanceof SDriveFolder;
        int imgBackColor = ((BaseApplication)context.getApplicationContext()).getResourcesUtils()
                                .parseFileTypeColor(context,isFolder);
        imgMain.setImageResource(((BaseApplication)context.getApplicationContext()).getResourcesUtils()
                                    .getFileIconRes((SDriveFile) so));
        imgMain.setBackgroundColor(imgBackColor);
        final SDriveFile sdf = (SDriveFile) so;

        name.setText(sdf.getName());

        //Folder or File Distringuish
        int visibility = isFolder ? View.GONE: View.VISIBLE;
        btnMenu.setVisibility(visibility);
        imgDrive1.setVisibility(visibility);
        imgDrive2.setVisibility(visibility);
        imgDrive3.setVisibility(visibility);


        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.buildFileMenu(sdf, context);
            }
        });

        //Resource Distinguish
        if (res == R.layout.view_holder_list_file ) {
            long filesize = sdf.getFileSize();
            date.setText(isFolder || filesize < 0? "" : DataUtils.fileSizeToString(filesize));
        }

        if (res == R.layout.view_holder_grid_file) {
            thumbnail.setBackgroundColor(imgBackColor);
            imgMain.setBackgroundColor(Color.parseColor(context.getString(R.string.transparent)));
        }

    }



}
