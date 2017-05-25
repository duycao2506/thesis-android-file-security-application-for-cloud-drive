package thesis.tg.com.s_cloud.data.from_third_party.task;

import android.os.AsyncTask;

import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.entity.SuperObject;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;

/**
 * Created by admin on 5/20/17.
 */

public class TransferTask extends SuperObject{
    TransferTaskManager manager;
    MyCallBack caller;
    protected int from;
    protected int to;
    protected SDriveFile file;

    public TransferTask() {
        super();
        this.manager = TransferTaskManager.getInstance();
        this.manager.add(this);
    }

    protected AsyncTask<Void,Void,Void> at = new AsyncTask<Void, Void, Void>(){
        @Override
        protected Void doInBackground(Void...params) {
            transfer();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            afterTransfer();
        }
    };

    protected void afterTransfer() {
        manager.remove(this);
    }

    protected void transfer() {

    }

    public int getType() {
        return 0;
    }

    public void  cancel(){
        this.at.cancel(true);
    }


}
