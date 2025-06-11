package com.iyuba.core.homepage.protocol;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/5/9.
 */
public class BBCNewsRequest extends BaseJSONRequest {

    public BBCNewsRequest(int num){
        setAbsoluteURI("http://apps."+com.iyuba.core.util.Constant.IYBHttpHead+"/minutes/titleApi.jsp?type=iphone&format=json&pages=1&parentID=1&pageNum=" +
                String.valueOf(num) +
                "&maxid=0");
    }
    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }

    @Override
    public BaseHttpResponse createResponse() {
        return new BBCNewsResponse();
    }
}
