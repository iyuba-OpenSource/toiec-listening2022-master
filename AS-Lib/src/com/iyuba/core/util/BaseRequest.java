package com.iyuba.core.util;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 打卡积分或红包
 */

public class BaseRequest extends BaseJSONRequest {

    public BaseRequest(String url) {


        setAbsoluteURI(url);

        Log.e("BaseRequest", url);
    }

    @Override
    public BaseHttpResponse createResponse() {
        return new BaseResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
