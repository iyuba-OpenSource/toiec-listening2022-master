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
 * Created by Administrator on 2017/1/3.
 */

public class GetRankListenInfoRequest extends BaseJSONRequest {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private String date = sdf.format(new Date());

    public GetRankListenInfoRequest(String uid, String type, String start, String total, String mode) {
//        String sign = URLEncoder.encode(uid + type + start + total + date);
        String sign = MD5.getMD5ofStr(uid + type + start + total + date);
        setAbsoluteURI("http://daxue."+com.iyuba.core.util.Constant.IYBHttpHead+"/ecollege/getStudyRanking.jsp?uid=" +
                uid +
                "&type=" +
                type +
                "&start=" +
                start +
                "&total=" +
                total +
                "&sign=" +
                sign +
                "&mode=" +
                mode);
        Log.e("GetRankListenInfoR", "http://daxue."+com.iyuba.core.util.Constant.IYBHttpHead+"/ecollege/getStudyRanking.jsp?uid=" +
                uid +
                "&type=" +
                type +
                "&start=" +
                start +
                "&total=" +
                total +
                "&sign=" +
                sign +
                "&mode=" +
                mode);

    }

    @Override
    public BaseHttpResponse createResponse() {
        return new GetRankListenInfoResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
