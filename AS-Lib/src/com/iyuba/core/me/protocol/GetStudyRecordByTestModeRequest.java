package com.iyuba.core.me.protocol;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jobbit by 2020/5/29.
 */

public class GetStudyRecordByTestModeRequest extends BaseJSONRequest {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private String date = sdf.format(new Date());

    public GetStudyRecordByTestModeRequest(String uid)
    {
        String sign = MD5.getMD5ofStr(uid + date);

        setAbsoluteURI("http://daxue."+com.iyuba.core.util.Constant.IYBHttpHead+"/ecollege/getStudyRecordByTestMode.jsp?format=json&uid="
                + uid
                + "&Pageth=1"
                + "&NumPerPage=10000"
                + "&TestMode=1"
                + "&sign="
                + sign);

        LogUtil.e("http://daxue."+com.iyuba.core.util.Constant.IYBHttpHead+"/ecollege/getStudyRecordByTestMode.jsp?format=json&uid="
                + uid
                + "&Pageth=1"
                + "&NumPerPage=10000"
                + "&TestMode=1"
                + "&sign="
                + sign);
    }

    @Override
    public BaseHttpResponse createResponse() {
        return  new GetStudyRecordByTestModeResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
