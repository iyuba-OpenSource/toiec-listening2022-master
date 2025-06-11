package com.iyuba.core.me.protocol;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.common.sqlite.mode.EvaluateRecord;
import com.iyuba.core.common.sqlite.mode.ExerciseRecord;
import com.iyuba.core.common.sqlite.mode.ListeningTestRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetVoaTestRecordResponse extends BaseJSONResponse {

    public String result = "";
    public String message = "";

    public List<EvaluateRecord> evaluateRecords =new ArrayList<>();

    @Override
    protected boolean extractBody(JSONObject headerElement, String bodyElement) {
//        Log.e("GetStudy-评测进度记录同步", "===================="+bodyElement);
        try {
            JSONObject jsonRoot = new JSONObject(bodyElement);
            result = jsonRoot.getString("result");
            JSONArray jsonArrayData = jsonRoot.getJSONArray("data");

            for (int i= 0;i<jsonArrayData.length();i++){
                EvaluateRecord evaluateRecord = new EvaluateRecord();
//                SpeakRankWork rankWork = new SpeakRankWork();
                JSONObject jsonElement = jsonArrayData.getJSONObject(i);
                evaluateRecord.sentence = jsonElement.getString("sentence");
                evaluateRecord.paraid = jsonElement.getString("paraid");
                evaluateRecord.score = jsonElement.getString("score");
                evaluateRecord.newsid = jsonElement.getString("newsid");
                evaluateRecord.idindex = jsonElement.getString("idindex");
                evaluateRecord.id = jsonElement.getString("id");
                evaluateRecord.createTime = jsonElement.getString("createTime");
                evaluateRecord.newstype = jsonElement.getString("newstype");
                evaluateRecords.add(evaluateRecord);
            }

//            if (message.equals("Success")) {
//
//                if (jsonRoot.has("result"))
//                    result = jsonRoot.getString("result");//---
//                if (jsonRoot.has("LessonId"))
//                    myLessonId = jsonRoot.getString("LessonId");//课程ID
//                if (jsonRoot.has("TestNumber"))
//                    myTestNumber = jsonRoot.getString("TestNumber"); //学习进度(秒数）
//                if (jsonRoot.has("TestWords"))
//                    myTestWords = jsonRoot.getString("TestWords");  //总单词数
//                if (jsonRoot.has("EndFlg"))
//                    myEndFlg = jsonRoot.getString("EndFlg"); //是否看完；1看完0没看完
//
//                listeningTestRecords = GsonUtils.toObjectList(
//                        jsonRoot.getString("data"), ListeningTestRecord.class);
//                Timber.e("听力同步数据：" + listeningTestRecords);
//            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
