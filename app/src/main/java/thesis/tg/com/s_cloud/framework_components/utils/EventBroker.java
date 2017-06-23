package thesis.tg.com.s_cloud.framework_components.utils;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.utils.ResourcesUtils;

/**
 * Created by Toan on 12/2/2016.
 */

public class EventBroker {
    public Map<String, Set<MyCallBack>> MyCallBacks;

    private static EventBroker mInstance;
    private BaseApplication ba;

    private EventBroker() {
        MyCallBacks = new HashMap<String, Set<MyCallBack>>();
    }

    public static EventBroker getInstance(BaseApplication application) {
        EventBroker eventBroker = application.getEventBroker();
        if (eventBroker == null) {
            eventBroker = new EventBroker();
            eventBroker.setBa(application);
        }
        return eventBroker;
    }

    public synchronized void register(MyCallBack MyCallBack, String event) {
        Set<MyCallBack> MyCallBackList = getMyCallBackSet(event);
        MyCallBackList.add(MyCallBack);
    }

    public synchronized void unRegister(MyCallBack MyCallBack, String event) {
        Set<MyCallBack> MyCallBackList = getMyCallBackSet(event);
        MyCallBackList.remove(MyCallBack);
    }

    private Set<MyCallBack> getMyCallBackSet(String event) {
        Set<MyCallBack> MyCallBackList = MyCallBacks.get(event);
        if (MyCallBackList == null) {
            MyCallBackList = new LinkedHashSet<MyCallBack>();
            MyCallBacks.put(event, MyCallBackList);
        }
        return MyCallBackList;
    }

    public synchronized void publish(String event,int code, Object data) {
        Set<MyCallBack> MyCallBackList = getMyCallBackSet(event);
        for (MyCallBack MyCallBack : MyCallBackList) {
            MyCallBack.callback(event, code, data);
        }
    }

    public void setBa(BaseApplication ba) {
        this.ba = ba;
    }
}
