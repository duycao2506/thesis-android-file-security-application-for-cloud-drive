package thesis.tg.com.s_cloud.framework_components.data.from_server;

import android.content.Context;

import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;


/**
 * Created by admin on 4/24/17.
 */

public class POSTRequestService extends RequestService {

    JSONObject jsonObject;

    byte[] body;


    public void setBody(byte[] body) {
        this.body = body;
    }


    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public POSTRequestService(Context context, String api, MyCallBack caller, GeneralResponse response) {
        super(context, api, caller, response);
    }

    @Override
    protected StringRequest buildStringRequest(String url) {
        if (jsonObject == null)
            return createRequestPOST(url,response,params,body,headers);
        return createRequestPOST( url, response, jsonObject, headers);
    }

}
