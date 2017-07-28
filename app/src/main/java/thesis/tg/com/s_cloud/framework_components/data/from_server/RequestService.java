package thesis.tg.com.s_cloud.framework_components.data.from_server;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import thesis.tg.com.s_cloud.framework_components.BaseApplication;
import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;


/**
 * Created by admin on 4/2/17.
 */

public class RequestService {
    Context context;
    protected Map<String, Object> params;
    protected Map<String, Object> headers;
    protected GeneralResponse response;
    private String api;

    public interface RequestServiceConstant {
        String BASE_URL = "https://scloud-server.herokuapp.com/";
//        String BASE_URL = "https://jsonplaceholder.typicode.com/";
        String api1 = "posts";
        String register = "auth/register";
        String login = "auth/login";
        String assign_root = "key/root";
        String request_verification = "devices/request-authorize";
        String profile = "auth/profile";
        String logout = "auth/logout";
        String request_for_otp = "devices/request-otp";
    }



    MyCallBack caller;
    AsyncTask<Void, Void, Void> requestTask;



    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public RequestService(Context context, String api, MyCallBack caller, GeneralResponse response) {
        this.context = context;
        this.api = api;
        this.caller = caller;
        this.response = response;
        this.params = new HashMap<>();
        this.headers = new HashMap<>();
    }

    public RequestService() {

    }


    public void executeService(){
        this.requestTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    request();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                caller.callback(RequestService.this.api,0,response);
            }
        };
        this.requestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private void request() throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append(RequestServiceConstant.BASE_URL);
        builder.append(api);
        StringRequest stringRequest = buildStringRequest(builder.toString());

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        BaseApplication ba = (BaseApplication) context.getApplicationContext();
        ba.getVolleyInstance().addToRequestQueue(stringRequest);
        while (!response.isGotResponse()) ;
        if (!response.isResponseError()) {
            return;
        }
        Log.e("RESPONSE",response.getResponse());
        throw new Exception(response.getError());
    }

    protected StringRequest buildStringRequest(String url) {
        return null;
    }


    //utils





    public static Map<String, Object> getBaseHeaders(){
        Map<String, Object> headers = new HashMap<String, Object>();
//        headers.put("Content-Type","application/json");
        return headers;
    }


    protected static StringRequest createRequestPOST(String url, GeneralResponse modelResponse, Map params,byte[] body, Map headers) {
        return new KasperStringRequest(Request.Method.POST, url, params, body, modelResponse, modelResponse, headers);
    }

    protected static StringRequest createRequestGET(String url, GeneralResponse modelResponse, Map params, Map headers) {
        String builtUrl = addParamstToGETRequest(url,params);
        return new KasperStringRequest(Request.Method.GET, builtUrl, null, modelResponse, modelResponse, headers);
    }

    protected static StringRequest createRequestPOST(String url, GeneralResponse modelResponse, JSONObject object, Map headers) {
        return new JsonBobyRequest(url, modelResponse, object, headers);
    }
    protected static StringRequest createRequestPOST(String url, GeneralResponse modelResponse, JSONArray object ,Map headers) {
        return new JsonBobyRequest(url, modelResponse, object, headers);
    }


    protected static String addParamstToGETRequest(String url, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }
        StringBuilder builder = new StringBuilder(url);
        builder.append("?");
        Set<String> keys = params.keySet();

        for (int i = 0; i < keys.size(); ++i) {

            Object key = keys.toArray()[i];
            Object val = params.get(keys.toArray()[i]);
            if (val instanceof ArrayList){
                ArrayList<Object> arr = (ArrayList) val;
                for (int j = 0; j < arr.size(); j++) {

                    builder.append(key);
                    builder.append("=");
                    builder.append(arr.get(j));
                    if (j < arr.size() - 1){
                        builder.append("&");
                    }
                }
            }else{
                builder.append(key);
                builder.append("=");
                builder.append(val);

            }
            if (i < keys.size() - 1) {
                builder.append("&");
            }
        }
        return builder.toString();
    }



}
