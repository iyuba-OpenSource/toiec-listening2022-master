package com.iyuba.core.me.protocol;

import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/11/2.
 */

public class ModifyUserNameResponse extends BaseJSONResponse {
    public String result = "";
    @Override
    protected boolean extractBody(JSONObject headerElement, String bodyElement) {
        try {
            JSONObject jsonObjectRoot = new JSONObject(bodyElement);
            result = jsonObjectRoot.getString("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }
}
