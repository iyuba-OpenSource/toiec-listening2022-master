/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.core.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.iyuba.configation.RuntimeManager;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.base.StudyRecordRequest;
import com.iyuba.core.common.protocol.base.StudyRecordResponse;
import com.iyuba.core.common.sqlite.mode.StudyRecord;

/**
 * 发现学习记录管理
 * 
 * @author 陈彤
 */
public class CommonStudyRecordUtil {
	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.CHINA);
	private static final Context mContext = RuntimeManager.getContext();
	private static int recordId;
	private static String recordStart;

//	public static void recordStart(int voaid) {
//		Constant.recordId = voaid;
//		Constant.recordStart = sdf.format(new Date());
//	}

	public static void groundRecordStart(int voaid) {
		recordId = voaid;
		recordStart = sdf.format(new Date());
	}

	public static void recordStop(String lesson, String flag) {
		StudyRecord studyRecord = new StudyRecord();
		studyRecord.endtime = sdf.format(new Date());
		studyRecord.flag = flag;
		String userid = "0";
		if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
			userid = AccountManagerLib.Instace(mContext).userId;
		}
		studyRecord.lesson = TextAttr.encode(TextAttr.encode(lesson));
		studyRecord.voaid = String.valueOf(recordId);
		studyRecord.starttime = recordStart;
		sendToNet(studyRecord, userid);
	}

	private static void sendToNet(final StudyRecord temp, String userid) {
		if (NetWorkState.isConnectingToInternet()) {
			ExeProtocol.exe(new StudyRecordRequest(userid, temp),
					new ProtocolResponse() {

						@Override
						public void finish(BaseHttpResponse bhr) {
							// TODO Auto-generated method stub
							Log.d("??", ((StudyRecordResponse) bhr).result);
						}

						@Override
						public void error() {
							// TODO Auto-generated method stub
						}
					});
		}
	}

	static Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				break;
			case 1:
				sendToNet((StudyRecord) msg.obj, msg.getData().get("id")
						.toString());
				break;
			}
		}
	};
}
