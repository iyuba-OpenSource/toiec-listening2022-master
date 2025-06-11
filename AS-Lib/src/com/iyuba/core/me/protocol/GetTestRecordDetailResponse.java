package com.iyuba.core.me.protocol;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.common.sqlite.mode.ExerciseRecord;
import com.iyuba.core.common.sqlite.mode.ListeningTestRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetTestRecordDetailResponse extends BaseJSONResponse {

    public String result = "";
    public String message = "";

    public List<ExerciseRecord> exerciseRecords =new ArrayList<>();

    @Override
    protected boolean extractBody(JSONObject headerElement, String bodyElement) {
//        Log.e("GetStudy-做题进度记录同步", "===================="+bodyElement);
        try {
            JSONObject jsonRoot = new JSONObject(bodyElement);
            result = jsonRoot.getString("result");
            message = jsonRoot.getString("message");
            JSONArray jsonArrayData = jsonRoot.getJSONArray("data");

            for (int i= 0;i<jsonArrayData.length();i++){
                ExerciseRecord exerciseRecord = new ExerciseRecord();
//                SpeakRankWork rankWork = new SpeakRankWork();
                JSONObject jsonElement = jsonArrayData.getJSONObject(i);
                exerciseRecord.lessonId = jsonElement.getString("LessonId");
                exerciseRecord.testNumber = jsonElement.getString("TestNumber");
                exerciseRecord.userAnswer = jsonElement.getString("UserAnswer");
                exerciseRecord.appName = jsonElement.getString("AppName");
                exerciseRecords.add(exerciseRecord);
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
