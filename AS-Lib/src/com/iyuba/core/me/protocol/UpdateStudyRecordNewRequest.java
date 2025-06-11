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

public class UpdateStudyRecordNewRequest extends BaseJSONRequest {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private String date = sdf.format(new Date());

    public UpdateStudyRecordNewRequest(String uid, String beginTime, String endTime, String endFlg,
                                       String device, String deviceId, String lessonId,
                                       String testNumber, String testWords, int rewardVersion) {
        String begin = beginTime.split(" ")[0];
        String sign =  MD5.getMD5ofStr(uid + begin +endTime);

        setAbsoluteURI("http://daxue." + WebConstant.CN_SUFFIX + "ecollege/updateStudyRecordNew.jsp" +
                "?appId=224" + "&appName=ToeicListening" + "&format=json" + "&platform=android" +
                "&BeginTime=" +
                beginTime +
                "&EndFlg=" +
                endFlg +
                "&DeviceId=" +
                deviceId +
                "&EndTime=" +
                endTime +
                "&Lesson=toeic" +
                "&LessonId=" +
                lessonId +
                "&TestMode=1" +
                "&TestNumber=" +
                testNumber +
                "&TestWords=" +
                testWords +
                "&uid=" +
                uid +
                "&sign=" +
                sign +
                "&rewardVersion=" + rewardVersion);

        LogUtil.e("http://daxue." + WebConstant.CN_SUFFIX + "ecollege/updateStudyRecordNew.jsp" +
                "?appId=224" + "&appName=ToeicListening" + "&format=json" + "&platform=android" +
                "&BeginTime=" +
                beginTime +
                "&EndFlg=" +
                endFlg +
                "&Device=" +
                device +
                "&DeviceId=" +
                deviceId +
                "&EndTime=" +
                endTime +
                "&Lesson=toeic" +
                "&LessonId=" +
                lessonId +
                "&TestMode=1" +
                "&TestNumber=" +
                testNumber +
                "&TestWords=" +
                testWords +
                "&uid=" +
                uid +
                "&sign=" +
                sign);
    }

    @Override
    public BaseHttpResponse createResponse() {
        return new GetRankInfoResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
