package com.iyuba.core.common.protocol.base;


import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.util.LoginResponse;
import com.iyuba.headnewslib.util.GsonUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class Login2Response extends BaseJSONResponse {
    public String result, uid, username, imgsrc, vip, validity, amount, email;
    public LoginResponse userInfo;

    @Override
    protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
        JSONObject jsonBody;
        try {
            jsonBody = new JSONObject(bodyElement);
            String isLogin = jsonBody.getString("isLogin");
            if (isLogin.equals("1")) {
                userInfo = GsonUtils.toObject(jsonBody.get("userinfo").toString(), LoginResponse.class);
            } else {

                userInfo = null;
            }

        } catch (JSONException e3) {
            // TODO Auto-generated catch block
            e3.printStackTrace();
            return false;
        }

        return true;
    }
}
