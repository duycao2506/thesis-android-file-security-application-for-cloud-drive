package thesis.tg.com.s_cloud.framework_components.data.from_server;

import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import thesis.tg.com.s_cloud.framework_components.utils.MyCallBack;

/**
 * Created by admin on 4/24/17.
 */

public class GETRequestService extends RequestService {
    public GETRequestService(String api, MyCallBack caller, GeneralResponse response) {
        super(api, caller, response);
    }

    @Override
    protected StringRequest buildStringRequest(String url) {
        return createRequestGET(url, response, this.params, this.headers);
    }
}
