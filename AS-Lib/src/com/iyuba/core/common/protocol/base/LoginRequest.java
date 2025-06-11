package com.iyuba.core.common.protocol.base;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户登录
 *
 * @author chentong
 */
public class LoginRequest extends BaseJSONRequest {
    private String userName, password;

    public LoginRequest(String userName, String password, String latitude,
                        String longitude) {
        this.userName = userName;
        this.password = password;
        setAbsoluteURI("http://api." + Constant.IYBHttpHead2 + "/v2/api.iyuba?protocol=11001&username="
                + this.userName
                + "&password="
                + MD5.getMD5ofStr(password)
                + "&x="
                + longitude
                + "&y="
                + latitude
                + "&appid="
                + Constant.APPID
                + "&sign="
                + MD5.getMD5ofStr("11001" + userName
                + MD5.getMD5ofStr(this.password) + "iyubaV2")
                + "&format=xml");
        Log.e("dsa", "http://api." + Constant.IYBHttpHead2 + "/v2/api.iyuba?protocol=11001&username="
                + this.userName
                + "&password="
                + MD5.getMD5ofStr(password)
                + "&x="
                + longitude
                + "&y="
                + latitude
                + "&appid="
                + Constant.APPID
                + "&sign="
                + MD5.getMD5ofStr("11001" + userName
                + MD5.getMD5ofStr(this.password) + "iyubaV2")
                + "&format=xml");
    }


    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {
        // TODO Auto-generated method stub
    }

    @Override
    public BaseHttpResponse createResponse() {
        // TODO Auto-generated method stub
        return new LoginResponse();
    }

}
