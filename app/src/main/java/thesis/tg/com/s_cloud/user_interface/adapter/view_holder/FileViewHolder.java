package thesis.tg.com.s_cloud.user_interface.adapter.view_holder;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;
import com.squareup.picasso.Picasso;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.from_third_party.DrivesManager;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.entities.SDriveFolder;
import thesis.tg.com.s_cloud.framework_components.entity.SuperObject;
import thesis.tg.com.s_cloud.framework_components.user_interface.adapter.EntityShower;
import thesis.tg.com.s_cloud.utils.DriveType;

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
                buildFileMenu(sdf);
            }
        });

    }


    private void buildFileMenu(final SDriveFile data) {

        BottomSheetMenuDialog dialog = new BottomSheetBuilder(this.context, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .setTitleTextColorResource(R.color.gray_dark_transparent)
                .addTitleItem(R.string.wtdn)
                .setIconTintColorResource(R.color.gray_dark_transparent)
                .addItem(R.id.upload,R.string.upload, ContextCompat.getDrawable(this.context,R.drawable.ic_cloud_upload_black_24dp))
                .addItem(R.id.sync,R.string.sync_with,ContextCompat.getDrawable(this.context,R.drawable.ic_sync_black_24dp))

                .setItemClickListener(new BottomSheetItemClickListener() {
                    @Override
                    public void onBottomSheetItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.upload:
                                buildDriveMenu(data,false);
                                break;
                            case R.id.sync:
                                buildDriveMenu(data, true);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .createDialog();
        dialog.setTitle(R.string.file_menu);
        dialog.show();
    }

    private void buildDriveMenu(final SDriveFile data, final boolean isSync) {
        BottomSheetMenuDialog dialog = new BottomSheetBuilder(this.context, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .addTitleItem(isSync ? R.string.sync_with : R.string.upload)
                .setTitleTextColorResource(R.color.gray_dark_transparent)
                .addItem(R.id.gdrive,R.string.g_drive,R.drawable.gdrive)
                .addItem(R.id.dbox,R.string.dbox,R.drawable.dbox)
                .addItem(R.id.local,R.string.local_storage,R.drawable.localstorage)
                .setItemClickListener(new BottomSheetItemClickListener() {
                    @Override
                    public void onBottomSheetItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.gdrive:
                                Toast.makeText(context,R.string.g_drive,Toast.LENGTH_SHORT).show();
                                DrivesManager.getInstance().transferDataTo(DriveType.GOOGLE,data,isSync);
                                break;
                            case R.id.dbox:
                                Toast.makeText(context,R.string.dbox,Toast.LENGTH_SHORT).show();
                                DrivesManager.getInstance().transferDataTo(DriveType.DROPBOX,data,isSync);
                                break;
                            case R.id.local:
                                DrivesManager.getInstance().transferDataTo(DriveType.LOCAL,data,isSync);
                                Toast.makeText(context,R.string.local_storage,Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                    }
                })
                .createDialog();
        dialog.setTitle(context.getString(R.string.which_drive));
        dialog.show();
    }
}
