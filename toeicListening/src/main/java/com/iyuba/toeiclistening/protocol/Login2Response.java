package com.iyuba.toeiclistening.protocol;

import com.iyuba.core.util.LoginResponse;
import com.iyuba.headnewslib.util.GsonUtils;
import com.iyuba.toeiclistening.frame.protocol.BaseJSONResponse;

import org.json.JSONException;
import org.json.JSONObject;


public class Login2Response extends BaseJSONResponse {
    public String result, uid, username, imgsrc, vip, validity, amount, email;
    public LoginResponse userInfo;

    @Override
    protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject(bodyElement);
        } catch (JSONException e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
        }
        userInfo = GsonUtils.toObject(bodyElement, LoginResponse.class);
        return false;
    }
}
