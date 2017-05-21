package thesis.tg.com.s_cloud.data.from_third_party.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 5/20/17.
 */

public class TransferTaskManager {
    private List<TransferTask> tasks;
    private TransferTaskManager(){
        tasks = new ArrayList<>();
    }

    private static TransferTaskManager instance;

    public static TransferTaskManager getInstance(){
        if (instance == null)
            instance = new TransferTaskManager();
        return instance;
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
