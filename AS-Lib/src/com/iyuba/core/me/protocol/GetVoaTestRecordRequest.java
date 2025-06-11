package com.iyuba.core.me.protocol;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.WebConstant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jobbit by 2020/5/29.
 */

public class GetVoaTestRecordRequest extends BaseJSONRequest {

    public GetVoaTestRecordRequest(String uid)
    {

        setAbsoluteURI(WebConstant.HTTP_SPEECH_ALL +"/management/getDetailInfo.jsp" +
                "?newstype=toeic" +
                "&userId=" +
                uid +
                "&language=English" +
                "&lastId=0" +
                "&pageCounts=10000"
                );

        LogUtil.e(WebConstant.HTTP_SPEECH_ALL +"/management/getDetailInfo.jsp" +
                "?newstype=toeic" +
                "&userId=" +
                uid +
                "&language=English" +
                "&lastId=0" +
                "&pageCounts=10000"
        );
    }

    @Override
    public BaseHttpResponse createResponse() {
        return  new GetVoaTestRecordResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
