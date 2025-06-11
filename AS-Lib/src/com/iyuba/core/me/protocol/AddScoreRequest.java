package com.iyuba.core.me.protocol;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 打卡积分或红包
 */

public class AddScoreRequest extends BaseJSONRequest {

    public AddScoreRequest(String userID, String appid, String time) {

        String uri="http://api."+com.iyuba.core.util.Constant.IYBHttpHead+"/credits/updateScore.jsp?srid=81&mobile=1" + "&uid=" + userID
                + "&appid=" + appid + "&flag=" + time;
        setAbsoluteURI(uri);

        Log.e("AddScoreRequest", "http://api."+com.iyuba.core.util.Constant.IYBHttpHead+"/credits/updateScore.jsp?srid=81&mobile=1" + "&uid=" + userID
                + "&appid=" + appid + "&flag=" + time);
    }

    @Override
    public BaseHttpResponse createResponse() {
        return new AddScoreResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
