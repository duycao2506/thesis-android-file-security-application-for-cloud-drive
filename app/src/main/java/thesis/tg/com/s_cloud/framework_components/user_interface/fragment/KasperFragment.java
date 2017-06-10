package thesis.tg.com.s_cloud.framework_components.user_interface.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.utils.EventBroker;
import thesis.tg.com.s_cloud.framework_components.utils.GlobalEventListennerDelegate;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;

/**
 * Created by admin on 4/2/17.
 */

public class KasperFragment extends Fragment implements GlobalEventListennerDelegate{



    String fragmentName;
    View parent;
    private int res;
    protected BaseApplication ba;

    public KasperFragment() {

    }

    public String getFragmentName() {
        return fragmentName;
    }

    public void setFragmentName(String fragmentName) {
        this.fragmentName = fragmentName;
    }


    public BaseApplication getBa() {
        return ba;
    }

    public void setBa(BaseApplication ba) {
        this.ba = ba;
    }

    /**
     * set custom layout for fragments
     * @param res
     */
    public void setResource(int res){
        this.res = res;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ba = (BaseApplication) getActivity().getApplication();
        View parent = inflater.inflate(res,container, false);

        onKasperViewCreate(parent);
        
        return parent;
    }

    /**
     *
     * init views from parent
     * @param parent
     */
    protected void onKasperViewCreate(View parent) {

    }

    @Override
    public void register(String[] events, MyCallBack caller) {
        for (String i : events){
            EventBroker.getInstance().register(caller, i);
        }

    }

    @Override
    public void unRegister(String[] events, MyCallBack caller) {
        for (String i : events){
            EventBroker.getInstance().unRegister(caller, i);
        }
    }
}
