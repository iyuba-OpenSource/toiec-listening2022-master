package com.iyuba.core.me.protocol;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by ivotsm on 2017/2/22.
 */

public class SignRequest extends BaseJSONRequest {

    public SignRequest(String uId) {


        setAbsoluteURI(String.format(Locale.CHINA,
                "http://daxue."+com.iyuba.core.util.Constant.IYBHttpHead+"/ecollege/getMyTime.jsp?uid=%s&day=%s&flg=1", uId, getDays()));

        Log.e("BookDetailRequest", String.format(Locale.CHINA,
                "http://daxue."+com.iyuba.core.util.Constant.IYBHttpHead+"/ecollege/getMyTime.jsp?uid=%s&day=%s&flg=1", uId, getDays()));
    }

    @Override
    public BaseHttpResponse createResponse() {
        return new SignResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }

    private long getDays() {
        //东八区;
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        cal.set(1970, 0, 1, 0, 0, 0);
        Calendar now = Calendar.getInstance(Locale.CHINA);
        long intervalMilli = now.getTimeInMillis() - cal.getTimeInMillis();
        long xcts = intervalMilli / (24 * 60 * 60 * 1000);
        return xcts;
    }
}
