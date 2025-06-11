package com.iyuba.core.me.protocol;

import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class SignResponse extends BaseJSONResponse {
    public JSONObject jsonObjectRoot;


    @Override
    protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
        try {
            jsonObjectRoot = new JSONObject(bodyElement);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

}
