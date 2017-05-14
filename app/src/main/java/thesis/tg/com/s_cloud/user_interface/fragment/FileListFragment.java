package thesis.tg.com.s_cloud.user_interface.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.api.services.drive.Drive;

import java.util.ArrayList;
import java.util.List;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.from_third_party.GoogleDriveWrapper;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.entity.SuperObject;
import thesis.tg.com.s_cloud.framework_components.user_interface.adapter.KasperRecycleAdapter;
import thesis.tg.com.s_cloud.framework_components.user_interface.fragment.KasperFragment;
import thesis.tg.com.s_cloud.framework_components.user_interface.fragment.RecycleViewFragment;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.user_interface.activity.HomeActivity;
import thesis.tg.com.s_cloud.user_interface.adapter.FileCollectionViewAdapter;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by admin on 4/26/17.
 */

public class FileListFragment extends RecycleViewFragment {

    public enum ViewMode {
        GRID,
        LIST
    }

    ViewMode vm;
    String[] globalEvents = {EventConst.FINISH_DOWNLOADING,EventConst.FINISH_UPLOADING};


    public ViewMode getVm() {
        return vm;
    }

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
        new AsyncTask<Void, Void, Boolean>() {
            int totalItemCount, lastVisibleItem, visibleThreshold = 1;
            final SwipeRefreshLayout finalswipe = swipeLayout;
            LinearLayoutManager layoutManager;

            @Override
            protected void onPreExecute() {
                layoutManager = (LinearLayoutManager) listView.getLayoutManager();
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                return (!listViewAdapter.isLoadingmore() && totalItemCount <= (lastVisibleItem + visibleThreshold));
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean && !swipeLayout.isRefreshing()) {
                    toListViewLoadingMode();
                    loadMore(FileListFragment.this);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


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
        listView.setAdapter(fcva);
        fcva.notifyDataSetChanged();
        listView.invalidate();

    }


    /**
     * Override List Fragment
     */

    @Override
    protected void loadMore(final MyCallBack caller) {
        GoogleDriveWrapper.getInstance().getFilesByFolderId(true, new MyCallBack() {
            @Override
            public void callback(String message, int code, Object data) {
                if (data != null) {
                    List<SDriveFile> tempFileList = (List<SDriveFile>) data;
                    listViewAdapter.addEntities(tempFileList);
                    listViewAdapter.notifyDataSetChanged();
                } else {
                    caller.callback(message, code, data);
                }
                caller.callback(HomeActivity.FINISH, 1, null);
            }
        });
    }

    @Override
    public void loadRefresh(final MyCallBack caller) {
        GoogleDriveWrapper.getInstance().getFilesByFolderId(false, new MyCallBack() {
            @Override
            public void callback(String message, int code, Object data) {
                if (data != null) {
                    List<SDriveFile> tempFileList = (List<SDriveFile>) data;
                    listViewAdapter.setEntities(new ArrayList<>());
                    listViewAdapter.addEntities(tempFileList);
                } else {
                    caller.callback(message, code, data);
                }
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
                Toast.makeText(this.getContext()
                        , "Finish downloading file "+((SDriveFile)data).getName()
                        ,Toast.LENGTH_SHORT).show();
                break;
            case EventConst.FINISH_UPLOADING:
                this.swipeLayout.setRefreshing(true);
                this.onRefresh();
                break;
            case HomeActivity.FINISH:
                this.swipeLayout.setRefreshing(false);
                this.listViewAdapter.setLoadingmore(false);
                this.listViewAdapter.notifyDataSetChanged();
                break;
            case EventConst.ERROR:
                String rawmess = (String) data;
                Toast.makeText(this.getActivity(), rawmess, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}


