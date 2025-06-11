package com.iyuba.core.me.protocol;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 打卡积分或红包
 */

public class AddScoreWordRequest extends BaseJSONRequest {

    public AddScoreWordRequest(String userID, String appid, String time,int index) {

        String uri="http://api."+com.iyuba.core.util.Constant.IYBHttpHead+"/credits/updateScore.jsp?srid=82&mobile=1" + "&uid=" + userID
                + "&appid=" + appid + "&flag=" + time+"idindex="+index;
        setAbsoluteURI(uri);

        Log.e("AddScoreWordRequest", "http://api."+com.iyuba.core.util.Constant.IYBHttpHead+"/credits/updateScore.jsp?srid=82&mobile=1" + "&uid=" + userID
                + "&appid=" + appid + "&flag=" + time+"idindex="+index);
    }

    @Override
    public BaseHttpResponse createResponse() {
        return new AddScoreResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
