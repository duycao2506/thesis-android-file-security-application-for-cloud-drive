package thesis.tg.com.s_cloud.data.from_third_party.task;

import android.os.AsyncTask;

import com.dropbox.core.DbxException;

import java.io.IOException;

import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.entity.SuperObject;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;

/**
 * Created by admin on 5/20/17.
 */

public class TransferTask extends SuperObject{
    TransferTaskManager manager;
    protected BaseApplication ba;
    MyCallBack caller;
    protected int from;
    protected int to;
    protected SDriveFile file;

    public void setFrom(int from) {
        this.from = from;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public TransferTask(BaseApplication ba) {
        super();
        this.ba = ba;
        this.manager = TransferTaskManager.getInstance();
        this.manager.add(this);
    }

    protected AsyncTask<Void,Void,Boolean> at = new AsyncTask<Void, Void, Boolean>(){
        @Override
        protected Boolean doInBackground(Void...params) {
            try {
                transfer();
            } catch (IOException | DbxException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            afterTransfer(aVoid);
        }
    };

    protected void afterTransfer(Boolean aVoid) {
        manager.remove(this);
    }

    protected void transfer() throws IOException, DbxException {

    }

    public int getType() {
        return 0;
    }

    public void  cancel(){
        this.at.cancel(true);
    }

    public void start(SDriveFile data){

    }


}
