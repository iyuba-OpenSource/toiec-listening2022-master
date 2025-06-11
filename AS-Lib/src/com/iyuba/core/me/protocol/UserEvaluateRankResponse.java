package com.iyuba.core.me.protocol;

import android.util.Log;

import com.iyuba.core.common.network.SpeakRankWork;
import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zh on 2019/5/13.
 */

public class UserEvaluateRankResponse extends BaseJSONResponse {
    public String result = "";
    public String message = "";
    public List<SpeakRankWork> comments = new ArrayList<>();
    @Override
    protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
        try {
            JSONObject jsonObjectRoot = new JSONObject(bodyElement);
            result= jsonObjectRoot.getString("result");
            message= jsonObjectRoot.getString("message");
            JSONArray jsonArrayData = jsonObjectRoot.getJSONArray("data");
            for (int i= 0;i<jsonArrayData.length();i++){
                SpeakRankWork rankWork = new SpeakRankWork();
                JSONObject jsonElement = jsonArrayData.getJSONObject(i);
                rankWork.id = jsonElement.getInt("id");
                rankWork.shuoshuo = jsonElement.getString("ShuoShuo");
                rankWork.shuoshuoType = jsonElement.getInt("shuoshuotype");
                rankWork.createdate = jsonElement.getString("CreateDate");
                rankWork.agreeCount = jsonElement.getInt("agreeCount");
                rankWork.againstCount = jsonElement.getInt("againstCount");
                rankWork.shuoshuoType=jsonElement.getInt("shuoshuotype");
                rankWork.score =jsonElement.getInt("score");
                rankWork.idindex =jsonElement.getInt("idIndex");
                rankWork.topicId = jsonElement.getInt("TopicId");
                comments.add(rankWork);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return true;
    }
}
