package thesis.tg.com.s_cloud.user_interface.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.androidanimations.library.Techniques;

import java.util.ArrayList;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.framework_components.user_interface.fragment.KasperFragment;
import thesis.tg.com.s_cloud.user_interface.adapter.FolderPathAdapter;
import thesis.tg.com.s_cloud.utils.UiUtils;

/**
 * Created by CKLD on 5/26/17.
 */

public class FolderPathFragment extends KasperFragment {

    ArrayList<String> folders;
    public FolderPathFragment() {
        this.setResource(R.layout.app_bar_extension);
        folders = new ArrayList<>();

    }

    RecyclerView horizontalRecView;
    private FolderPathAdapter fpa;
    View par;

    @Override
    protected void onKasperViewCreate(View parent) {
        super.onKasperViewCreate(parent);
        par = parent;
        horizontalRecView = (RecyclerView) parent.findViewById(R.id.lvFolderCursor);
        LinearLayoutManager llm = new LinearLayoutManager(this.getContext());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        horizontalRecView.setLayoutManager(llm);

        fpa = new FolderPathAdapter(this.getContext(),R.layout.view_holder_folder_cursor,folders);
        horizontalRecView.setAdapter(fpa);

    }

    public void addFolder(String folderName){
        ArrayList<String> names = new ArrayList<>();
        names.add(folderName);
        folders.addAll(names);
        if (fpa != null) {
            this.fpa.notifyDataSetChanged();
            this.horizontalRecView.smoothScrollToPosition(fpa.getItemCount()-1);
        }
    }

    public void backFolderStepSize(int s){
        this.fpa.removeFromLastAmount(s);
        this.fpa.notifyDataSetChanged();
    }

    public void refreshWithFolder(String foldername){
        ArrayList<String> names = new ArrayList<>();
        names.add(foldername);
        this.folders = names;
        if (fpa != null) {
            this.fpa.setEntities(folders);
            this.fpa.notifyDataSetChanged();
        }
    }

    public void hideBarExtension() {
        if (par.getVisibility() == View.GONE) return;
        UiUtils.ClosingAnimate(this.par, Techniques.SlideOutUp, 300);
    }

    public void showBarExtension(){
        if (par.getVisibility() == View.VISIBLE) return;
        UiUtils.OpeningAnimate(this.par, Techniques.SlideInDown, 300);
    }
}
