package com.iyuba.core.common.protocol.base;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.mob.secverify.datatype.VerifyResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * 用户登录
 *
 * @author chentong
 */
public class Login2Request extends BaseJSONRequest {
    private String userName, password;

    public Login2Request(VerifyResult data) {
        String token;
        token = URLEncoder.encode(data.getToken());
        token = URLEncoder.encode(token);   //续加码2次方可成功
//            token = data.getToken();
//        token = data.getToken();
        setAbsoluteURI("http://api." + Constant.IYBHttpHead2 + "/v2/api.iyuba"
                + "?protocol=10010"
                + "&token=" + token
                + "&appId=" + Constant.APPID
                + "&opToken=" + data.getOpToken()
                + "&appkey=" + Constant.SMSAPPKEY
                + "&operator=" + data.getOperator());

    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {
        // TODO Auto-generated method stub
    }

    @Override
    public BaseHttpResponse createResponse() {
        // TODO Auto-generated method stub
        return new Login2Response();
    }

}
