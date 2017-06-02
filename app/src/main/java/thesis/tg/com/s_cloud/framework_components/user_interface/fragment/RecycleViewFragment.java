package thesis.tg.com.s_cloud.framework_components.user_interface.fragment;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import thesis.tg.com.s_cloud.R;
import thesis.tg.com.s_cloud.framework_components.user_interface.adapter.KasperRecycleAdapter;
import thesis.tg.com.s_cloud.framework_components.user_interface.fragment.KasperFragment;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;


/**
 * Created by admin on 4/2/17.
 */

public class RecycleViewFragment extends KasperFragment implements OnRefreshListener, MyCallBack {

    protected RecyclerView listView;
    protected KasperRecycleAdapter listViewAdapter;
    protected SwipeRefreshLayout swipeLayout;

    public RecycleViewFragment() {
        setResource(R.layout.fragment_recycle_view);
    }

    @Override
    protected void onKasperViewCreate(View parent) {
        super.onKasperViewCreate(parent);

        //Swipe
        swipeLayout = (SwipeRefreshLayout) parent.findViewById(R.id.swipeRecycle);

        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeLayout.setOnRefreshListener(this);


        //ListView
        listView = (RecyclerView) parent.findViewById(R.id.lvRecycle);
        if (listViewAdapter == null)
            listViewAdapter = initListViewAdapter();
        listView.setAdapter(listViewAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);
        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int dy;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                this.dy = dy;
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    if (dy < 0)
                        scrollUpEvent(recyclerView);
                    else if (dy > 0)
                        scrollDownEvent(recyclerView);
                }

            }
        });
    }

    protected void scrollUpEvent(RecyclerView recyclerView) {

    }

    protected void scrollDownEvent(RecyclerView recyclerView) {

    }




    protected void toListViewLoadingMode() {
        listViewAdapter.setLoadingmore(true);
        listViewAdapter.notifyItemInserted(listViewAdapter.getItemCount()-1);
    }

    /*
    *
    * override method to custom view holder
     */
    protected KasperRecycleAdapter initListViewAdapter() {
        return new KasperRecycleAdapter(getContext(),android.R.layout.activity_list_item, new ArrayList());
    }


    @Override
    public void onRefresh() {
        loadRefresh(this);
    }


    /**
     * Called when refreshing the list
     */
    protected void loadRefresh(MyCallBack caller) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;

            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                swipeLayout.setRefreshing(false);
            }
        }.execute();
    }


    /**
     *
     * called when add more entities to the end of the list
     */
    protected void loadMore(MyCallBack caller){

    }


    /**
     *  called when pulling data from somewhere
     */
    protected void loadEntities(MyCallBack caller){

    }

    @Override
    public void callback(String message, int code, Object data) {

    }


}
