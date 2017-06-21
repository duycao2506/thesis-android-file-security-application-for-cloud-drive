package thesis.tg.com.s_cloud.data.from_third_party.task;

import com.dropbox.core.util.Collector;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import thesis.tg.com.s_cloud.framework_components.BaseApplication;

/**
 * Created by admin on 5/20/17.
 */

public class TransferTaskManager {
    private List<TransferTask> tasks;
    private TransferTaskManager(){
        tasks = new ArrayList<>();
    }

    private static TransferTaskManager instance;

    public static TransferTaskManager getInstance(BaseApplication baseApplication){
        if (baseApplication.getTransferTaskManager() == null)
            return  new TransferTaskManager();
        return baseApplication.getTransferTaskManager();
    }

    public void remove(TransferTask transferTask) {
        tasks.remove(transferTask);
    }

    public void add(TransferTask downloadTask) {
        tasks.add(downloadTask);
    }

    public List<TransferTask> getTasks() {
        return tasks;
    }
}
