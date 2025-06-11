package com.iyuba.core.homepage.protocol;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/5/10.
 */
public class HeadNewsRequest extends BaseJSONRequest {
    public HeadNewsRequest(int num){
        setAbsoluteURI("http://cms."+com.iyuba.core.util.Constant.IYBHttpHead+"/cmsApi/getMyNewsList.jsp?&level=0&pageCounts=" +
                String.valueOf(num) +
                "&maxId=0&format=json");
    }
    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }

    @Override
    public BaseHttpResponse createResponse() {
        return new HeadNewsResponse();
    }
}
