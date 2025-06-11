package com.iyuba.core.me.protocol;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.common.sqlite.mode.ListeningTestRecord;
import com.iyuba.core.common.sqlite.mode.test.WordsUpdateRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import com.iyuba.headnewslib.util.GsonUtils;

/**
 * Created by Administrator on 2017/1/3.
 */

public class GetWordsResponse extends BaseJSONResponse {

    public String result = "";

    public String myTestId = "";
    public String myScore = "";
    public String myUserAnswer = "";
    public String myUpdateTime = "";
    public String Lesson = "";

    public List<WordsUpdateRecord> wordsUpdateRecord =new ArrayList<>();

    @Override
    protected boolean extractBody(JSONObject headerElement, String bodyElement) {
//        Log.e("GetStudy-单词学习记录同步", "===================="+bodyElement);
        try {
            JSONObject jsonRoot = new JSONObject(bodyElement);
            result = jsonRoot.getString("result");
            JSONArray jsonArrayData = jsonRoot.getJSONArray("data");

            for (int i= 0;i<jsonArrayData.length();i++){
                WordsUpdateRecord wordsRecord = new WordsUpdateRecord();
                JSONObject jsonElement = jsonArrayData.getJSONObject(i);
                wordsRecord.testId = jsonElement.getString("TestId");
                wordsRecord.score = jsonElement.getString("Score");
                wordsRecord.userAnswer = jsonElement.getString("UserAnswer");
                wordsRecord.updateTime = jsonElement.getString("UpdateTime");
                wordsRecord.lesson = jsonElement.getString("Lesson");

                wordsUpdateRecord.add(wordsRecord);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
