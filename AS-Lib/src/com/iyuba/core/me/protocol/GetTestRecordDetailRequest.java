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
 * Created by jobbit by 2020/5/29.
 */

public class GetTestRecordDetailRequest extends BaseJSONRequest {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private String date = sdf.format(new Date());

    public GetTestRecordDetailRequest(String uid)
    {
        String sign = MD5.getMD5ofStr(uid + date);

        setAbsoluteURI("http://daxue."+ WebConstant.CN_SUFFIX +"ecollege/getTestRecordDetail.jsp" +
                "?NumPerPage=10000" + "&format=json" + "&Pageth=1" + "&TestMode=1"+
                "&uid=" +
                uid +
                "&sign=" +
                sign);

        LogUtil.e("http://daxue."+ WebConstant.CN_SUFFIX +"ecollege/getTestRecordDetail.jsp" +
                "?NumPerPage=10000" + "&format=json" + "&Pageth=1" + "&TestMode=1"+
                "&uid=" +
                uid +
                "&sign=" +
                sign);
    }

    @Override
    public BaseHttpResponse createResponse() {
        return  new GetTestRecordDetailResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
