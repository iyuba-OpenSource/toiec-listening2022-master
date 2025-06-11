package com.iyuba.core.common.network;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class AgreeAgainstRequest extends BaseJSONRequest {


    public AgreeAgainstRequest(String protocol, String commnetId, int type) {
        super();
        if (type == 2000) {//多句合成点赞接口不好用？ 走遍美国可以
            setAbsoluteURI("http://daxue."+Constant.IYBHttpHead+"/appApi/UnicomApi?"
                    + "protocol=" + protocol
                    + "&id=" + commnetId);
            Log.e("zanRequest", "http://daxue."+Constant.IYBHttpHead+"/appApi/UnicomApi?"
                    + "protocol=" + protocol
                    + "&id=" + commnetId);
        } else {
            setAbsoluteURI("http://voa."+Constant.IYBHttpHead+"/voa/UnicomApi?"
                    +"id=" + commnetId
                    + "&protocol=" + protocol);
            Log.e("zanRequest", "http://voa."+Constant.IYBHttpHead+"/voa/UnicomApi?id="
                    + commnetId + "&protocol=" + protocol);
        }
        //http://voa.iyuba.cn/voa/UnicomApi?protocol=61001&id=8765910
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }

    @Override
    public BaseHttpResponse createResponse() {
        return new AgreeAgainstResponse();
    }

}
