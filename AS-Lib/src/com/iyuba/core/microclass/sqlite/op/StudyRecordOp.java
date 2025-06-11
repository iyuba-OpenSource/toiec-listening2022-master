package com.iyuba.core.microclass.sqlite.op;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.core.common.sqlite.db.DatabaseService;
import com.iyuba.core.microclass.sqlite.mode.StudyRecordInfo;

public class StudyRecordOp extends DatabaseService {
	
	//StudyRecord 表的字段
	public static final String TABLE_NAME_STUDYRECORD = "StudyRecord";
	public static final String _ID = "_id";
	public static final String UID = "uid";
	public static final String APPID = "appId";
	public static final String APPNAME = "appName";
	public static final String BEGINTIME = "BeginTime";
	public static final String ENDTIME = "EndTime";
	public static final String LESSON = "Lesson";
	public static final String LESSONID = "LessonId";
	public static final String TESTNUMBER = "TestNumber";
	public static final String ENDFLG = "EndFlg";
	public static final String DEVICE = "Device";
	public static final String DEVICEID = "DeviceId";
	public static final String IP = "IP";
	public static final String UPDATETIME = "updateTime";
	public static final String ISUPLOAD = "IsUpload";

	public StudyRecordOp(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * 添加到学习记录
	 * 
	 * @param StudyRecordInfo
	 * @return
	 */
	public void saveStudyRecord(StudyRecordInfo studyRecordInfo) {
		String sqlString = "insert or replace into StudyRecord(uid,appId,appName,BeginTime,EndTime,Lesson,LessonId,"
				+"TestNumber,EndFlg,Device,IP,updateTime,DeviceId,IsUpload)"+" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] objects=new Object[]{studyRecordInfo.uid,studyRecordInfo.appId,studyRecordInfo.appName,
				studyRecordInfo.BeginTime,studyRecordInfo.EndTime,studyRecordInfo.Lesson,studyRecordInfo.LessonId,
				studyRecordInfo.TestNumber,studyRecordInfo.EndFlg,studyRecordInfo.Device,studyRecordInfo.IP,
				studyRecordInfo.updateTime,studyRecordInfo.DeviceId,studyRecordInfo.IsUpload};
		
		//未添加用户名的查询命令
//		String sqlString = "insert into FavoriteWord(word,audio,pron,def,CreateDate)"
//				+" values(?,?,?,?,?)";
//		Object[] objects=new Object[]{newWord.Word,
//				newWord.audio,newWord.pron,newWord.def,newWord.CreateDate};
		
//		Log.d("NewWord Content:", "word: " + newWord.Word + " audio:" 
//				+ newWord.audio + " pron:" + newWord.pron + " def:" 
//				+ newWord.def + " userName:" + newWord.userName + " CreateDate"
//				+ newWord.CreateDate);
		
		importDatabase.openDatabase().execSQL(sqlString,objects);
	}
	
	/*
	 * 查询未被上传的学习记录
	 * 
	 * @return List<StudyRecord>
	 * */
	
	public ArrayList<StudyRecordInfo> getWillUploadStudyRecord(){
		ArrayList<StudyRecordInfo> studyRecordList = new ArrayList<StudyRecordInfo>();
		String sqlSting = "select * from StudyRecord" + " where IsUpload='0'";
		Cursor cursor = null;
		try {
			cursor = importDatabase.openDatabase().rawQuery(sqlSting, null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				for (int i = 0; i < cursor.getCount(); i++) {
					StudyRecordInfo studyRecordInfo = new StudyRecordInfo();
					// 获取StudyRecord表中的数据
					
					studyRecordInfo.uid = cursor.getString(cursor
							.getColumnIndex(UID));
					studyRecordInfo.appId = cursor.getString(cursor
							.getColumnIndex(APPID));
					studyRecordInfo.appName = cursor.getString(cursor
							.getColumnIndex(APPNAME));
					studyRecordInfo.BeginTime = cursor.getString(cursor
							.getColumnIndex(BEGINTIME));
					studyRecordInfo.EndTime = cursor.getString(cursor
							.getColumnIndex(ENDTIME));
					studyRecordInfo.Lesson = cursor.getString(cursor
							.getColumnIndex(LESSON));
					studyRecordInfo.LessonId = cursor.getString(cursor
							.getColumnIndex(LESSONID));
					studyRecordInfo.TestNumber = cursor.getString(cursor
							.getColumnIndex(TESTNUMBER));
					studyRecordInfo.EndFlg = cursor.getString(cursor
							.getColumnIndex(ENDFLG));
					studyRecordInfo.Device = cursor.getString(cursor
							.getColumnIndex(DEVICE));
					studyRecordInfo.IP = cursor.getString(cursor
							.getColumnIndex(IP));
					studyRecordInfo.updateTime = cursor.getString(cursor
							.getColumnIndex(UPDATETIME));
					studyRecordInfo.DeviceId = cursor.getString(cursor
							.getColumnIndex(DEVICEID));
					studyRecordInfo.IsUpload = Boolean.parseBoolean(cursor
							.getString(cursor.getColumnIndex(ISUPLOAD)));

					studyRecordList.add(i, studyRecordInfo);
					cursor.moveToNext();
				}
			}
			cursor.close();
		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return studyRecordList;
		
	}
	
	/**
	 * 
	 * 更新StudyRecord中记录的上传标志
	 * 
	 * @param appId
	 * @param BeginTime
	 * @return
	 */
	public void setIsUpload(String appId, String BeginTime) {
		String sqlString = "update StudyRecord" + " set IsUpload = 'true'"
				+ " where appId = '" + appId +"' and BeginTime = '" + BeginTime+"'";
		
		importDatabase.openDatabase().execSQL(sqlString);
	}
	
}
