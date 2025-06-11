package com.iyuba.core.microclass.protocol;

import android.util.Log;

import com.android.volley.Response;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.util.Base64Coder;
import com.iyuba.core.me.pay.BaseJsonObjectRequest;
import com.iyuba.core.me.pay.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

public class AddCreditsRequestNew extends BaseJsonObjectRequest {
    private static final String TAG = AddCreditsRequest.class.getSimpleName();
    public String result;
    public int addCredit;
    public int totalCredit;
    public String message = "";

    public AddCreditsRequestNew(final int uid, int voaid, int srid,
                                final RequestCallBack rc) {
        super(Constant.addCreditsUrl + "srid=" + srid
                + "&uid=" + uid + "&appid=" + Constant.APPID
                + "&idindex=" + voaid + "&mobile=1" + "&flag=" + "1234567890" + Base64Coder.getTime());
        setResListener(new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonBody) {
                Log.e(TAG, jsonBody.toString());
                try {
                    result = jsonBody.getString("result");
                    Log.e("score", result);
                    if (isRequestSuccessful()) {
                        String addcred = jsonBody.getString("addcredit");
                        if (!"".equals(addcred))
                            addCredit = Integer.parseInt(addcred);
                        else
                            addCredit = 0;
                        String total = jsonBody.getString("totalcredit");
                        if (!"".equals(total))
                            totalCredit = Integer.parseInt(total);
                        else
                            totalCredit = 0;
                    } else {
                        message = jsonBody.getString("message");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                rc.requestResult(AddCreditsRequestNew.this);
            }
        });
    }

    @Override
    public boolean isRequestSuccessful() {
        return ("200".equals(result)) ? true : false;
    }

    public boolean isShareFirstlySuccess() {
        return isRequestSuccessful();
    }

    public boolean isShareRepeatlySuccess() {
        return ("201".equals(result)) ? true : false;
    }
}