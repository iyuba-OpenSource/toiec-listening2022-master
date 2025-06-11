package com.iyuba.core.me.protocol;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 口语排行榜
 */

public class GetRankSpeakInfoRequest extends BaseJSONRequest {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private String date = sdf.format(new Date());

    public GetRankSpeakInfoRequest(String uid, String type, String start, String total, String topic, String topicid, String shuoshuotype) {
//        String sign = URLEncoder.encode(uid + type + start + total + date);

        //
        String sign = MD5.getMD5ofStr(uid + topic + topicid  + start + total + date);
        setAbsoluteURI("http://daxue."+com.iyuba.core.util.Constant.IYBHttpHead+"/ecollege/getTopicRanking.jsp?uid=" +
                uid +
                "&type=" +
                type +
                "&start=" +
                start +
                "&total=" +
                total +
                "&sign=" +
                sign +
                "&topic=" +
                topic +
                "&topicid=" +
                topicid +
                "&shuoshuotype=" +
                shuoshuotype);
        Log.e("GetRankSpeakInfoRequest", "http://daxue."+com.iyuba.core.util.Constant.IYBHttpHead+"/ecollege/getTopicRanking.jsp?uid=" +
                uid +
                "&type=" +
                type +
                "&start=" +
                start +
                "&total=" +
                total +
                "&sign=" +
                sign +
                "&topic=" +
                topic +
                "&topicid=" +
                topicid +
                "&shuoshuotype=" +
                shuoshuotype);

    }

    @Override
    public BaseHttpResponse createResponse() {
        return new GetRankSpeakInfoResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
