package thesis.tg.com.s_cloud.framework_components.utils;

/**
 * Created by admin on 5/13/17.
 */

public interface GlobalEventListennerDelegate {
    public void register(String[] events, MyCallBack caller);
    public void unRegister(String[] events, MyCallBack caller);
}
