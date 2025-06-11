package com.iyuba.core.homepage.protocol;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/5/10.
 */
public class SongsRequest extends BaseJSONRequest {
    public SongsRequest(int num){
        setAbsoluteURI("http://apps."+com.iyuba.core.util.Constant.IYBHttpHead+"/afterclass/getSongList.jsp?pageNum=1&pageCounts=" +
                String.valueOf(num));
    }
    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }

    @Override
    public BaseHttpResponse createResponse() {
        return new SongsResponse();
    }
}
