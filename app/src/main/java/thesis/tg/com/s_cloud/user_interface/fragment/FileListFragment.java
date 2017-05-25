package thesis.tg.com.s_cloud.user_interface.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.CloudDriveWrapper;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.user_interface.adapter.KasperRecycleAdapter;
import thesis.tg.com.s_cloud.framework_components.user_interface.fragment.RecycleViewFragment;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.user_interface.activity.HomeActivity;
import thesis.tg.com.s_cloud.user_interface.adapter.FileCollectionViewAdapter;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by admin on 4/26/17.
 */

public class FileListFragment extends RecycleViewFragment {


    private String folder;


    public enum ViewMode {
        GRID,
        LIST
    }

    int driveType;
    ViewMode vm;
    String[] globalEvents = {EventConst.FINISH_DOWNLOADING,EventConst.FINISH_UPLOADING};



    FileCollectionViewAdapter fcva;
    LinearLayoutManager llm;
    GridLayoutManager glm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        register(globalEvents, this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegister(globalEvents,this);

    }



    @Override
    protected void onKasperViewCreate(View parent) {
        super.onKasperViewCreate(parent);
        llm = (LinearLayoutManager) this.listView.getLayoutManager();
        glm = new GridLayoutManager(this.getContext(), 2);

        if (vm == ViewMode.GRID) {
            this.listView.setLayoutManager(glm);
        } else {
            this.listView.setLayoutManager(llm);
        }

    }

    @Override
    protected KasperRecycleAdapter initListViewAdapter() {
        fcva = new FileCollectionViewAdapter(new ArrayList<SDriveFile>(), R.layout.view_holder_list_file, getContext());
        return fcva;
    }

    @Override
    protected void scrollEvent(RecyclerView recyclerView, int dx, int dy) {
        if (listViewAdapter.isLoadingmore() || swipeLayout.isRefreshing()) return;
        int totalItemCount, lastVisibleItem, visibleThreshold = 1;
        LinearLayoutManager layoutManager;
        layoutManager = (LinearLayoutManager) listView.getLayoutManager();
        totalItemCount = layoutManager.getItemCount();
        lastVisibleItem = layoutManager.findLastVisibleItemPosition();
        if (totalItemCount <= (lastVisibleItem + visibleThreshold)  ) {
            toListViewLoadingMode();
            loadMore(FileListFragment.this);
        }
    }

    public void changeViewMode(ViewMode vm) {
        int layout;
        this.vm = vm;
        switch (vm) {
            case GRID:
                this.listView.setLayoutManager(glm);
                fcva.setViewholder_res(R.layout.view_holder_grid_file);
                break;
            case LIST:
                this.listView.setLayoutManager(llm);
                fcva.setViewholder_res(R.layout.view_holder_list_file);
                break;
        }
        if (!this.isVisible()) return;
        listView.setAdapter(fcva);
        fcva.notifyDataSetChanged();
        listView.invalidate();

    }


    /**
     * Override List Fragment
     */

    @Override
    protected void loadMore(final MyCallBack caller) {
        CloudDriveWrapper.getInstance(driveType).getFilesInTopFolder(true, new MyCallBack() {
            @Override
            public void callback(String message, int code, Object data) {
                if (data != null) {
                    List<SDriveFile> tempFileList = (List<SDriveFile>) data;
                    if (tempFileList.size() > 0)
                        listViewAdapter.addEntities(tempFileList);
                } else {
                    caller.callback(message, code, null);
                }
                caller.callback(HomeActivity.FINISH, 1, null);
            }
        });
    }

    @Override
    public void loadRefresh(final MyCallBack caller) {
        CloudDriveWrapper.getInstance(driveType).getFilesInTopFolder(false, new MyCallBack() {
            @Override
            public void callback(String message, int code, Object data) {
                if (data != null) {
                    List<SDriveFile> tempFileList = (List<SDriveFile>) data;
                    listViewAdapter.setEntities(new ArrayList<>());
                    listViewAdapter.addEntities(tempFileList);
                } else {
                    if (caller != null)
                        caller.callback(message, code, data);
                }
                if (caller != null)
                    caller.callback(HomeActivity.FINISH, 1, null);
            }
        });
    }

    @Override
    protected void loadEntities(final MyCallBack caller) {
        super.loadEntities(this);
    }

    @Override
    public void callback(String message, int code, Object data) {
        switch (message) {
            case EventConst.FINISH_DOWNLOADING:
                if (code != this.driveType) return;
                Toast.makeText(this.getContext()
                        , "Finish downloading file "+((SDriveFile)data).getName()
                        ,Toast.LENGTH_SHORT).show();
                break;
            case EventConst.FINISH_UPLOADING:
                if (code != this.driveType) return;
                this.swipeLayout.setRefreshing(true);
                this.onRefresh();
                break;
            case HomeActivity.FINISH:
                this.swipeLayout.setRefreshing(false);
                listViewAdapter.setLoadingmore(false);
                listViewAdapter.notifyDataSetChanged();
                break;
            case EventConst.ERROR:
                String rawmess = (String) data;
                Toast.makeText(this.getActivity(), rawmess, Toast.LENGTH_SHORT).show();
                break;
        }
    }


    /**
     * Getters and Setters
     * @return
     */

    public ViewMode getVm() {
        return vm;
    }
    public int getDriveType() {
        return driveType;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getFolder() {
        return folder;
    }



    /**
     * File list fragment builder
     */

    public static class Builder {
        private FileListFragment flf;
        private int driveType;

        public Builder() {
            flf = new FileListFragment();
        }

        public Builder setFragmentName(String name) {
            flf.setFragmentName(name);
            return this;
        }

        public Builder setFolder(String folder) {
            flf.setFolder(folder);
            return this;
        }

        public Builder setViewMode(ViewMode vm){
            flf.vm = vm;
            return this;
        }

        public Builder setDriveType(int driveType){
            flf.driveType = driveType;
            return this;
        }
        public FileListFragment build(){
            return flf;
        }
    }
}


