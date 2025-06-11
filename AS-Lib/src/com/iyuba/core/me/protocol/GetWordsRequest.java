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

public class GetWordsRequest extends BaseJSONRequest {

    public GetWordsRequest(String uid)
    {
        setAbsoluteURI("http://daxue."+ WebConstant.CN_SUFFIX +"ecollege/getPassHomePage.jsp?Lesson=toeic&pageNumber=1" +
                        "&pageCounts=2000&chooseType=detail&userId="
                        + uid
                        );

        LogUtil.e("http://daxue."+ WebConstant.CN_SUFFIX +"ecollege/getPassHomePage.jsp?Lesson=toeic&pageNumber=1" +
                "&pageCounts=2000&chooseType=detail&userId="
                + uid
        );
    }

    @Override
    public BaseHttpResponse createResponse() {
        return  new GetWordsResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
