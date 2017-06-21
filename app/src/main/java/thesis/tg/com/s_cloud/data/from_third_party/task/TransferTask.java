package thesis.tg.com.s_cloud.data.from_third_party.task;

import android.os.AsyncTask;

import com.dropbox.core.DbxException;

import java.io.IOException;

import thesis.tg.com.s_cloud.entities.SDriveFile;
import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.entity.SuperObject;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;
import thesis.tg.com.s_cloud.utils.EventConst;

/**
 * Created by admin on 5/20/17.
 */

public class TransferTask extends SuperObject implements MyCallBack{
    TransferTaskManager manager;
    protected BaseApplication ba;
    MyCallBack caller;
    protected int from;
    protected int to;
    protected SDriveFile file;
    protected long finishSize;

    public long getFinishSize() {
        return finishSize;
    }

    public void setFinishSize(long finishSize) {
        this.finishSize = finishSize;
    }

    public int getProgressPercent(){
        return (int) (100*(this.finishSize*1.0/this.file.getFileSize()));
    }

    public void setCaller(MyCallBack caller) {
        this.caller = caller;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public TransferTask(BaseApplication ba) {
        super();
        this.ba = ba;
        this.manager = TransferTaskManager.getInstance(ba);
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
        this.file = data;
        at.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void callback(String message, int code, Object data) {
        switch (message){
            case EventConst.PROGRESS_UPDATE:
                if ((long) data > this.finishSize) {
                    this.finishSize = (long) data;
                    if (this.caller == null) break;
                    this.caller.callback(message, 1, this);
                }
                break;
            default:
                break;
        }
    }


    public SDriveFile getFile() {
        return file;
    }
}
