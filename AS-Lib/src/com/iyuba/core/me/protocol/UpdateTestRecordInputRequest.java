package com.iyuba.core.me.protocol;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.WebConstant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jobbit by 2020/5/29.
 */

public class UpdateTestRecordInputRequest extends BaseJSONRequest {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private String date = sdf.format(new Date());

    public UpdateTestRecordInputRequest(String uid, String beginTime, String testTime,
                                        String answerResult,
                                        String deviceId, String lessonId, String rightAnswer,
                                        String userAnswer, String testNumber)
    {
        String begin = beginTime.split(" ")[0];
        String sign = MD5.getMD5ofStr(uid + "iyubaTest" + date);

        setAbsoluteURI("http://daxue."+ WebConstant.CN_SUFFIX +"ecollege/updateTestRecordInput.jsp" +
                "?DeviceId=" +
                deviceId +
                "&TestMode=1" +
                "&appId=224" +
                "&appName=ToeicListening" +

                "&datalist=" + "{[" +
                "&AnswerResut=" +
                answerResult +
                "AppName=ToeicListening" +
                "&BeginTime=" +
                beginTime +
                "&LessonId=" +
                lessonId +
                "&RightAnswer=" +
                rightAnswer +
                "&TestNumber=" +
                testNumber +
                "&TestTime=" +
                testTime +
                "&UserAnswer=" +
                userAnswer +
                "&uid=" +
                uid +
                "]}" +

                "&Format=json" +
                "&uid=" +
                uid +
                "&sign=" +
                sign);

        LogUtil.e("?http://daxue."+ WebConstant.CN_SUFFIX +"ecollege/updateTestRecordInput.jsp" +
                "?DeviceId=" +
                deviceId +
                "&TestMode=1" +
                "&appId=224" +
                "&appName=ToeicListening" +

                "&datalist=" + "{[" +
                "&AnswerResut=" +
                answerResult +
                "AppName=ToeicListening" +
                "&BeginTime=" +
                beginTime +
                "&LessonId=" +
                lessonId +
                "&RightAnswer=" +
                rightAnswer +
                "&TestNumber=" +
                testNumber +
                "&TestTime=" +
                testTime +
                "&UserAnswer=" +
                userAnswer +
                "&uid=" +
                uid +
                "]}" +

                "&Format=json" +
                "&uid=" +
                uid +
                "&sign=" +
                sign);
    }

    @Override
    public BaseHttpResponse createResponse() {
        return  new GetRankInfoResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
