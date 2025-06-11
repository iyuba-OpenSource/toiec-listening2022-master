package com.iyuba.core.me.protocol;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zH on 2017/3/13.
 */

public class UserEvaluateRankRequest extends BaseJSONRequest {
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public UserEvaluateRankRequest(String uid, String topic, String voaId) {
        String sign = MD5.getMD5ofStr(uid + "getWorksByUserId" + df.format(new Date()));
        setAbsoluteURI("http://voa."+Constant.IYBHttpHead+"/voa/getWorksByUserId.jsp?uid=" +
                uid +
                "&topic=" +
                topic +
                "&shuoshuoType=2,4&sign=" +
                sign +
                "&topicId=" +
                voaId);
        Log.e("userComment", "http://voa."+Constant.IYBHttpHead+"/voa/getWorksByUserId.jsp?uid=" +
                uid +
                "&topic=" +
                topic +
                "&shuoshuoType=2,4&sign=" +
                sign +
                "&topicId=" +
                voaId);
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }

    @Override
    public BaseHttpResponse createResponse() {
        return new UserEvaluateRankResponse();
    }
}
