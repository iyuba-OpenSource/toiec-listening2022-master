package com.iyuba.core.me.protocol;


import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.WebConstant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ivotsm on 2017/2/22.
 */

public class RankRequest extends BaseJSONRequest {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private String date = sdf.format(new Date());

    public RankRequest(String uid, String topic, String topicid, String start, String total) {


        String sign = MD5.getMD5ofStr(uid + topic + topicid + start + total + date);

        setAbsoluteURI("http://daxue."+ WebConstant.CN_SUFFIX +"ecollege/getTopicRanking.jsp" +
                                "?topic=" +
                                topic +
                                "&topicid=" +
                                topicid +
                                "&uid=" +
                                uid +
                                "&type=" +
                                "D" +
                               "&start=" +
                                start +
                                "&total=" +
                               total +
                                "&sign=" +
                                sign);

        LogUtil.e("http://daxue."+ WebConstant.CN_SUFFIX +"ecollege/getTopicRanking.jsp?topic=" +
                topic +
                "&topicid=" +
                topicid +
                "&uid=" +
                uid +
                "&type=" +
                "D" +
                "&start=" +
                start +
                "&total=" +
                total +
                "&sign=" +
                sign);
    }

    @Override
    public BaseHttpResponse createResponse() {
        return  new GetRankInfoResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
