package thesis.tg.com.s_cloud.framework_components.data.from_server;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Toan on 12/2/2016.
 */

public class KasperStringRequest extends com.android.volley.toolbox.StringRequest {

    private Map<String, String> _params;

    private Map<String, String> _customheaders;

    private byte[] body;

    public KasperStringRequest(int method, String url, Map<String, String> params, byte[] body, Response.Listener<String> listener,
                               Response.ErrorListener errorListener, Map headers) {
        super(method, url, listener, errorListener);
        _params = params;
        _customheaders = headers;
        this.body = body;
    }

    public KasperStringRequest(int method, String url, Map<String, String> params, Response.Listener<String> listener,
                         Response.ErrorListener errorListener, Map<String, String> headers) {
        super(method, url, listener, errorListener);
        _params = params;
        _customheaders = headers;
    }

    @Override
    protected Map<String, String> getParams() {
        if (_params == null)
            _params = new HashMap<String, String>();
        return _params;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        return super.parseNetworkResponse(response);
    }

    /* (non-Javadoc)
     * @see com.android.volley.Request#getHeaders()
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();
        if(_customheaders != null)
            return _customheaders;

        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }

        return headers;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (body == null)
            return super.getBody();
        return body;
    }
}