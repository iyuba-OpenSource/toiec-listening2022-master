package com.iyuba.core.homepage.protocol;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/5/13.
 */
public class HeadNewsSearchRequest extends BaseJSONRequest {

    public HeadNewsSearchRequest(String id){
        setAbsoluteURI("http://cms."+com.iyuba.core.util.Constant.IYBHttpHead+"/cmsApi/getNews.jsp?NewsIds="+id);
    }
    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }

    @Override
    public BaseHttpResponse createResponse() {
        return new HeadNewsSearchResponse();
    }
}
