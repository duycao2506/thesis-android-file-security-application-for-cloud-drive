package thesis.tg.com.s_cloud.user_interface.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.data.CloudDriveWrapper;
import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.user_interface.activity.KasperActivity;
import thesis.tg.com.s_cloud.framework_components.user_interface.adapter.KasperRecycleAdapter;
import thesis.tg.com.s_cloud.framework_components.user_interface.fragment.RecycleViewFragment;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.user_interface.activity.HomeActivity;
import thesis.tg.com.s_cloud.user_interface.adapter.FileCollectionViewAdapter;
import thesis.tg.com.s_cloud.utils.DriveType;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by admin on 4/26/17.
 */

public class FileListFragment extends RecycleViewFragment {


    private String folder;
    private List<SDriveFile> dataList;


    public enum ViewMode {
        GRID,
        LIST
    }

    int driveType;
    ViewMode vm;
    String[] globalEvents = {EventConst.FINISH_DOWNLOADING,EventConst.FINISH_UPLOADING, EventConst.FAIL_TRANSFER};



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
        if (dataList == null){
            dataList = new ArrayList<>();
        }
        listViewAdapter.setEntities(dataList);
        fcva.initFilter(new FileListFilter(dataList, fcva));
        llm = (LinearLayoutManager) this.listView.getLayoutManager();
        glm = new GridLayoutManager(this.getContext(), 2);

        if (vm == ViewMode.GRID) {
            this.listView.setLayoutManager(glm);
            this.fcva.setViewholder_res(R.layout.view_holder_grid_file);
        } else {
            this.listView.setLayoutManager(llm);
            this.fcva.setViewholder_res(R.layout.view_holder_list_file);
        }

    }

    @Override
    protected KasperRecycleAdapter initListViewAdapter() {
        fcva = new FileCollectionViewAdapter(new ArrayList<SDriveFile>(), R.layout.view_holder_list_file, getContext());

        return fcva;
    }

    @Override
    protected void scrollDownEvent(RecyclerView recyclerView) {

        ((MyCallBack) getContext()).callback(EventConst.SCROLL_DOWN, 1, null);

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

    @Override
    protected void scrollUpEvent(RecyclerView recyclerView) {
        ((MyCallBack) getContext()).callback(EventConst.SCROLL_UP, 1, null);
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
        ba.getDriveWrapper(driveType).getFilesInTopFolder(true, new MyCallBack() {
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
        ba.getDriveWrapper(driveType).getFilesInTopFolder(false, new MyCallBack() {
            @Override
            public void callback(String message, int code, Object data) {
                if (data != null) {
                    List<SDriveFile> tempFileList = (List<SDriveFile>) data;
                    dataList = tempFileList;
                    if (listViewAdapter != null && isVisible()) {
                        listViewAdapter.setEntities(dataList);
                    }
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
        String announcement = "";
        switch (message) {
            case EventConst.FINISH_DOWNLOADING:
                if (code != this.driveType && !isVisible())
                    break;
                this.swipeLayout.setRefreshing(true);
                announcement = getString(R.string.finish_downfile) + " " + ((SDriveFile)data).getName();
                this.onRefresh();
                break;
            case EventConst.FINISH_UPLOADING:
                if (code != this.driveType && !isVisible())
                    break;
                this.swipeLayout.setRefreshing(true);
                announcement = getString(R.string.finish_upfile)+ " "  + ((SDriveFile)data).getName();
                this.onRefresh();
                break;
            case HomeActivity.FINISH:
                this.swipeLayout.setRefreshing(false);
                listViewAdapter.setLoadingmore(false);
                listViewAdapter.notifyDataSetChanged();
                break;
            case EventConst.ERROR:
                announcement = (String) data;
                break;
            case EventConst.INPUT_FOLDER_NAME_FIN:
                if (code == EventConst.SUCCESS){
                    ba.getDriveWrapper(driveType).requestNewFolder(data.toString(),this.folder,this);
                }
                break;
            case EventConst.CREATE_FOLDER:
                announcement = getString(R.string.create_folder) + " " + data.toString() + " ";
                if (code == EventConst.SUCCESS) {
                    announcement += " " +getString(R.string.successfully);
                    this.onRefresh();
                }else{
                    announcement += " " +getString(R.string.unsuccessfully);
                }
                break;
            case EventConst.DELETE_FILE:
                announcement =  getString(R.string.delete) +" " +  data.toString()  + " " ;
                if (code == EventConst.SUCCESS) {
                    announcement += " " + getString(R.string.successfully);
                    this.onRefresh();
                } else
                    announcement += " "+ getString(R.string.unsuccessfully);

                break;
            case EventConst.FAIL_TRANSFER:
                announcement = getString(R.string.transfer_file)
                        + ((SDriveFile)data).getName()
                        + " "
                        + getString(R.string.unsuccessfully);
                break;


        }
        if (announcement.length() == 0) return;
        ((KasperActivity)getContext()).callback(EventConst.MESSAGE,1,announcement);
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


    public void createNewFolder(){
        NameInputFragment df = new NameInputFragment();
        df.setCaller(this);
        df.show(this.getChildFragmentManager(),EventConst.INPUT_FOLDER_NAME_FIN);
    }


    /**
     * File List Filter
     *
     */

    public class FileListFilter extends Filter {

        private List<SDriveFile> sDriveFileList;
        private List<SDriveFile> filterSfileList;
        private FileCollectionViewAdapter adapter;

        public FileListFilter(List<SDriveFile> contactList, FileCollectionViewAdapter adapter) {
            this.adapter = adapter;
            this.sDriveFileList = contactList;
            this.filterSfileList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filterSfileList.clear();
            final FilterResults results = new FilterResults();

            //here you need to add proper items do filterChatUserList
            for (final SDriveFile item : sDriveFileList) {
                if (item.getName().toLowerCase().trim().contains(constraint.toString().toLowerCase())) {
                    filterSfileList.add(item);
                }
            }

            results.values = filterSfileList;
            results.count = filterSfileList.size();
            return results;
        }

        public void resetFilterList(List<SDriveFile> list){
            this.sDriveFileList = list;
            this.filterSfileList = new ArrayList<>();
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.setFilterList(filterSfileList);
            adapter.notifyDataSetChanged();
        }

        public void setBaseList(List baseList) {
            this.sDriveFileList = baseList;
        }
    }

    public void updateSearchText(String content){
        fcva.filterList(content);
    }


    /**
     * File list fragment builder
     */

    public static class Builder {
        private FileListFragment flf;

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

        public Builder setBa(BaseApplication ba) {
            flf.ba = ba;
            return this;
        }
    }
}


