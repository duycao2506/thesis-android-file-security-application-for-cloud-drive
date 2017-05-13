package thesis.tg.com.s_cloud.framework_components.data.from_server;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;


public class JsonBobyRequest extends StringRequest {

    JSONObject body;
    JSONArray bodyArr;

    Map headers;

    public JsonBobyRequest(String url, GeneralResponse response, JSONObject body, Map headers) {
        super(Method.POST, url, response, response);
        this.headers = headers;
        this.body = body;
    }

    public JsonBobyRequest(String url, GeneralResponse response, JSONArray body, Map headers) {
        super(Method.POST, url, response, response);
        this.headers = headers;
        this.bodyArr = body;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return body != null ? body.toString().getBytes() : bodyArr.toString().getBytes();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (headers != null)
            return headers;
        return super.getHeaders();
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }
}
