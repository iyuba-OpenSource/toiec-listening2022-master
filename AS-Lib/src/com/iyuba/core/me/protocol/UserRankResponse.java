package com.iyuba.core.me.protocol;

import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class UserRankResponse extends BaseJSONResponse {
    public String result, totalUser, totalTime, positionByTime, totalTest,
            positionByTest, totalRate, positionByRate, everyDayInfo;

    @Override
    protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
        // TODO Auto-generated method stub
        JSONObject jsonObjectRoot;
        try {
            jsonObjectRoot = new JSONObject(bodyElement);
            result = jsonObjectRoot.getString("result");
            totalUser = jsonObjectRoot.getString("totalUser");
            totalTime = jsonObjectRoot.getString("totalTime");
            positionByTime = jsonObjectRoot.getString("positionByTime");
            totalTest = jsonObjectRoot.getString("totalTest");
            positionByTest = jsonObjectRoot.getString("positionByTest");
            totalRate = jsonObjectRoot.getString("totalRate");
            positionByRate = jsonObjectRoot.getString("positionByRate");
            everyDayInfo = jsonObjectRoot.getString("everyDayInfo");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

}
