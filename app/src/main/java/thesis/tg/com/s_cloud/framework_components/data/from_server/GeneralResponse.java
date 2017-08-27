package thesis.tg.com.s_cloud.framework_components.data.from_server;

import android.util.Base64;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by admin on 4/2/17.
 */

public class GeneralResponse implements Response.Listener<String>, Response.ErrorListener {
    private boolean gotResponse;
    private boolean responseError = false;
    private String error;
    private String response;

    @Override
    public void onErrorResponse(VolleyError error) {
        gotResponse = true;
        responseError = true;
        this.error = error.getMessage();
        if(error.networkResponse != null)
            this.response = new String(error.networkResponse.data);
        else
            this.response = "{\n" +
                    "  \t\"message\": \""+"No internet connection"+"\",\n" +
                    "    \"status\":"+"failed"+",\n" +
                    "}";
    }

    @Override
    public void onResponse(String response) {
        this.response = response;
        gotResponse = true;
        responseError = false;
    }

    public String getError() {
        return this.error;
    }

    public boolean isGotResponse() {
        return gotResponse;
    }

    public boolean isResponseError() {
        return responseError;
    }

    public String getResponse(){
        return response;
    }
}
