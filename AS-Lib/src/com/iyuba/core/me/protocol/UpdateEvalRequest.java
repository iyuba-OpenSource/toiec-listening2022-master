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

public class UpdateEvalRequest extends BaseJSONRequest {

    public UpdateEvalRequest(int userId, String idIndex, int newsId, int paraId,
                             String sentence)
    {

        setAbsoluteURI(WebConstant.HTTP_SPEECH_ALL +"/test/eval/" +
                "?type=toeic" +
                "&userId=" +
                userId +
                "&IdIndex=" +
                idIndex +
                "&newsId=" +
                newsId +
                "&paraId=" +
                paraId +
                "&sentence=" +
                sentence );

        LogUtil.e(WebConstant.HTTP_SPEECH_ALL +"/test/eval/" +
                "?type=toeic" +
                "&userId=" +
                userId +
                "&IdIndex=" +
                idIndex +
                "&newsId=" +
                newsId +
                "&paraId=" +
                paraId +
                "&sentence=" +
                sentence );
    }

    @Override
    public BaseHttpResponse createResponse() {
        return  new GetRankInfoResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
