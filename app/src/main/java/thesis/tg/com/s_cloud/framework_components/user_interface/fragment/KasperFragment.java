package thesis.tg.com.s_cloud.framework_components.user_interface.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by admin on 4/2/17.
 */

public class KasperFragment extends Fragment {
    View parent;
    private int res;

    public KasperFragment() {

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
}
