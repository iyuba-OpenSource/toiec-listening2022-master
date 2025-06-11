package com.iyuba.core.me.protocol;

import android.util.Log;

import com.iyuba.core.common.network.SpeakRankWork;
import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.common.sqlite.mode.ListeningTestRecord;
import com.iyuba.core.common.sqlite.mode.StudyRecord;
import com.iyuba.core.me.sqlite.mode.RankBean;
import com.iyuba.core.me.sqlite.mode.StudyTimeBeanNew;
import com.iyuba.headnewslib.util.GsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

//import com.iyuba.headnewslib.util.GsonUtils;

/**
 * Created by Administrator on 2017/1/3.
 */

public class GetStudyRecordByTestModeResponse extends BaseJSONResponse {

    public String result = "";
    public String message = "";

    public String myLessonId = "";
    public String myTestNumber = "";
    public String myEndFlg = "";
    public String myTestWords = "";

    public List<ListeningTestRecord> listeningTestRecords =new ArrayList<>();

    @Override
    protected boolean extractBody(JSONObject headerElement, String bodyElement) {
//        Log.e("GetStudy-听力学习记录同步", "===================="+bodyElement);
        try {
            JSONObject jsonRoot = new JSONObject(bodyElement);
            result = jsonRoot.getString("result");
            message = jsonRoot.getString("message");
            JSONArray jsonArrayData = jsonRoot.getJSONArray("data");

            for (int i= 0;i<jsonArrayData.length();i++){
                ListeningTestRecord listenRecord = new ListeningTestRecord();
//                SpeakRankWork rankWork = new SpeakRankWork();
                JSONObject jsonElement = jsonArrayData.getJSONObject(i);
                listenRecord.lessonId = jsonElement.getString("LessonId");
                listenRecord.testNumber = jsonElement.getString("TestNumber");
                listenRecord.testWords = jsonElement.getString("TestWords");
                listenRecord.endFlg = jsonElement.getString("EndFlg");
                listeningTestRecords.add(listenRecord);
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
