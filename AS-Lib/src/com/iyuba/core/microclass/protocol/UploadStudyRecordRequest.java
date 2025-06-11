package com.iyuba.core.microclass.protocol;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.microclass.sqlite.mode.StudyRecordInfo;

public class UploadStudyRecordRequest extends BaseJSONRequest{

	public UploadStudyRecordRequest(StudyRecordInfo studyRecordInfo){
		try {
			setAbsoluteURI("http://daxue."+com.iyuba.core.util.Constant.IYBHttpHead+"/ecollege/updateStudyRecord.jsp?format=json&uid="+studyRecordInfo.uid
					+"&BeginTime="+URLEncoder.encode(studyRecordInfo.BeginTime,"UTF-8")+"&EndTime="+URLEncoder.encode(studyRecordInfo.EndTime,"UTF-8")
					+"&Lesson="+URLEncoder.encode(URLEncoder.encode(studyRecordInfo.Lesson, "UTF-8"), "UTF-8")
					+"&LessonId="+studyRecordInfo.LessonId+"&EndFlg="+studyRecordInfo.EndFlg+"&platform="+URLEncoder.encode(studyRecordInfo.Device, "UTF-8")
					+"&IP="+studyRecordInfo.IP+"&appName="+studyRecordInfo.appName+"&appId="+studyRecordInfo.appId
					+"&DeviceId="+studyRecordInfo.DeviceId+"&testNumber="+studyRecordInfo.TestNumber);
//			setAbsoluteURI(URLEncoder.encode("http://daxue."+com.iyuba.core.util.Constant.IYBHttpHead+"/ecollege/updateStudyRecord.jsp?format=json&uid="+studyRecordInfo.uid
//					+"&BeginTime="+studyRecordInfo.BeginTime+"&EndTime="+studyRecordInfo.EndTime+"&Lesson="+URLEncoder.encode(studyRecordInfo.Lesson,"UTF-8")
//					+"&LessonId="+studyRecordInfo.LessonId+"&EndFlg="+studyRecordInfo.EndFlg+"&platform="+studyRecordInfo.Device
//					+"&IP="+studyRecordInfo.IP+"&appName="+studyRecordInfo.appName+"&appId="+studyRecordInfo.appId
//					+"&DeviceId="+studyRecordInfo.DeviceId+"&testNumber="+studyRecordInfo.TestNumber,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		try {
//			Log.d("UploadStudyRecordRequest url", "http://daxue."+com.iyuba.core.util.Constant.IYBHttpHead+"/ecollege/updateStudyRecord.jsp?format=json&uid="+studyRecordInfo.uid
//					+"&BeginTime="+studyRecordInfo.BeginTime+"&EndTime="+studyRecordInfo.EndTime+"&Lesson="+URLEncoder.encode(studyRecordInfo.Lesson,"UTF-8")
//					+"&LessonId="+studyRecordInfo.LessonId+"&EndFlg="+studyRecordInfo.EndFlg+"&platform="+studyRecordInfo.Device
//					+"&IP="+studyRecordInfo.IP+"&appName="+studyRecordInfo.appName+"&appId="+studyRecordInfo.appId
//					+"&DeviceId="+studyRecordInfo.DeviceId+"&testNumber="+studyRecordInfo.TestNumber);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	
	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new UploadStudyRecordResponse();
	}
	
}
