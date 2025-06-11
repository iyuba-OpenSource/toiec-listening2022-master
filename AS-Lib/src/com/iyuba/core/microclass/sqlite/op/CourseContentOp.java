package com.iyuba.core.microclass.sqlite.op;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.iyuba.core.common.sqlite.db.DatabaseService;
import com.iyuba.core.microclass.sqlite.mode.CourseContent;
import com.iyuba.core.microclass.sqlite.mode.FirstTitleInfo;
import com.iyuba.core.microclass.sqlite.mode.SecondTitleInfo;

public class CourseContentOp extends DatabaseService {
	
	//CourseContent表
	public static final String TABLE_NAME_COURSECONTENT = "CourseContent";
	public static final String ID = "id";
	public static final String COST = "cost";
//	public static final String DESC = "desc";
	public static final String TITLENAME = "titleName";
	public static final String TOTALTIME = "totaltime";
//	public static final String CREDIT = "credit";
//	public static final String VIEWCOUNT = "viewCount";
	
	public static final String ISALLDOWNLOAD = "IsAllDownload";
	public static final String ALLPROGRESS = "AllProgress";
	public static final String ISAUDIODOWNLOAD = "IsAudioDownload";
	public static final String AUDIOPROGRESS = "AudioProgress";
	public static final String ISVIDEODOWNLOAD = "IsVideoDownload";
	public static final String VIDEOPROGRESS = "VideoProgress";
	public static final String ISFREE = "IsFree";
	public static final String PACKID = "PackId";
	public static final String LESSON = "lesson";
	public static final String VIDEO = "video";
	
	public static final String BTID = "btid";
	public static final String BTNAME = "btname";
	
//	public static final String TID = "tid";
	
	public CourseContentOp(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * 参考例子
	 * */
	
//	public int selectStudyTime(String userId) {
//		Cursor cursor = importDatabase.openDatabase().rawQuery(
//				"select " + STUDYTIME + " from " + TABLE_NAME + " where "
//						+ USERID + "=?", new String[] { userId });
//		if (cursor.moveToNext()) {
//			int temp = cursor.getInt(0);
//			if (cursor != null) {
//				cursor.close();
//			}
//			closeDatabase(null);
//			return temp;
//		}
//		if (cursor != null) {
//			cursor.close();
//		}
//		closeDatabase(null);
//		return 0;
//	}
//
//	public synchronized void updataByStudyTime(String userid, int time) {
//		importDatabase.openDatabase().execSQL(
//				"update " + TABLE_NAME + " set " + STUDYTIME + "=" + time
//						+ " where " + USERID + "=?", new String[] { userid });
//		closeDatabase(null);
//	}
//	
//	
//	public void saveData(UserInfo userInfo) {
//		importDatabase.openDatabase()
//				.execSQL(
//						"insert or replace into " + TABLE_NAME + " (" + USERID
//								+ "," + GENDER + "," + FANS + "," + ATTENTIONS
//								+ "," + NOTIFICATIONS + "," + SEETIMES + ","
//								+ STATE + "," + VIP + "," + IYUBI + ","
//								+ DEADLINE + "," + STUDYTIME + "," + POSITION
//								+ "," + ICOIN
//								+ ") values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
//						new Object[] { userInfo.uid, userInfo.gender,
//								userInfo.follower, userInfo.following,
//								userInfo.notification, userInfo.views,
//								userInfo.text, userInfo.vipStatus,
//								userInfo.iyubi, userInfo.deadline,
//								userInfo.studytime, userInfo.position,
//								userInfo.icoins });
//		closeDatabase(null);
//	}
	
	
	/**
	 * 
	 * 查找移动课堂的某个课程包的一级标题
	 * @return
	 */
	public ArrayList<FirstTitleInfo> findCourseContentFirTitleBySpecial(String packId) {
		ArrayList<FirstTitleInfo> firCourseTitles = new ArrayList<FirstTitleInfo>();
		
		Cursor cursor = null;
		try {
			cursor = importDatabase.openDatabase().rawQuery(
					"select DISTINCT " + BTNAME+","+BTID +" from " + TABLE_NAME_COURSECONTENT + " where "+ PACKID + " = "+ packId
					 + " ORDER BY " + BTID +" asc"
					, new String[] {});
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				Log.d("查找移动课堂的某个课程包的一级标题", "执行");
				FirstTitleInfo firTitleInfo = new FirstTitleInfo();
				firTitleInfo.btname = cursor.getString(0);
				firTitleInfo.btid = cursor.getInt(1);
				firCourseTitles.add(firTitleInfo);
			}
		} 
		catch (Exception e) {
		} 
		finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		Log.e("CourseFirTitle SEPCIAL:", firCourseTitles.size()+" ");		
		return firCourseTitles;		
	}
	
	/**
	 * 
	 * 查找移动课堂的某个课程包的一级标题下的二级标题
	 * @return
	 */
	public ArrayList<SecondTitleInfo> findCourseContentSecTitleBySpecial(String btid) {
		ArrayList<SecondTitleInfo> secCourseTitles = new ArrayList<SecondTitleInfo>();
		Cursor cursor = null;
		try {
			cursor = importDatabase.openDatabase().rawQuery(
					"select " + TITLENAME+","+ID +" from " + TABLE_NAME_COURSECONTENT + " where "+ BTID + " = "+ btid
							, new String[] {});
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				Log.d("查找移动课堂的某个课程包的一级标题", "执行");
				SecondTitleInfo secTitleInfo = new SecondTitleInfo();
				
				secTitleInfo.titleName = cursor.getString(0);
				secTitleInfo.id = cursor.getInt(1);
				secCourseTitles.add(secTitleInfo);
			}
		} 
		catch (Exception e) {
		} 
		finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		Log.e("CourseSecitle SEPCIAL:", secCourseTitles.size()+" ");		
		return secCourseTitles;		
	}
	
	
	/**
	 * 
	 * 查找移动课堂的某个TitleId对应的CourseContent对象
	 * @return
	 */
	public CourseContent findCourseContentBySpecial(String TitleId) {
		CourseContent course = new CourseContent();
		Cursor cursor = null;
		try {
			cursor = importDatabase.openDatabase().rawQuery(
					"select *" + " from " + TABLE_NAME_COURSECONTENT + " where "+ ID + " = "+ TitleId
							, new String[] {});
			if (cursor.moveToFirst()) {
				course.id = cursor.getInt(0);
				course.titleName = cursor.getString(1);
				course.cost = cursor.getDouble(2);
				course.IsFree = Boolean.parseBoolean(cursor.getString(3));
				course.PackId = cursor.getInt(4);
				course.lesson = cursor.getString(5);
				course.btid = cursor.getInt(6);
				course.btname = cursor.getString(7);
				course.video = cursor.getInt(8);
				course.IsAllDownload = cursor.getInt(9);
				course.IsAudioDownload = cursor.getInt(10);
				course.IsVideoDownload = cursor.getInt(11);
				course.AllProgress = cursor.getFloat(12);
				course.AudioProgress = cursor.getFloat(13);
				course.VideoProgress = cursor.getFloat(14);
				course.totaltime = cursor.getString(15);
				
			}
		} 
		catch (Exception e) {
		} 
		finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return course;		
	}
	
	/**
	 * 
	 * 查找移动课堂的某个课程包的一级标题下的二级标题对应的CourseContent对象
	 * @return
	 */
	public ArrayList<CourseContent> findCourseContentBTidBySpecial(String btid) {
		
		ArrayList<CourseContent> courses = new ArrayList<CourseContent>();
		Cursor cursor = null;
		try {
			cursor = importDatabase.openDatabase().rawQuery(
					"select *" + " from " + TABLE_NAME_COURSECONTENT + " where "+ BTID + " = "+ btid + " ORDER BY " + ID +" asc"
							, new String[] {});
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				CourseContent course = new CourseContent();
				course.id = cursor.getInt(0);
				course.titleName = cursor.getString(1);
				course.cost = cursor.getDouble(2);
				course.IsFree = Boolean.parseBoolean(cursor.getString(3));
				course.PackId = cursor.getInt(4);
				course.lesson = cursor.getString(5);
				course.btid = cursor.getInt(6);
				course.btname = cursor.getString(7);
				course.video = cursor.getInt(8);
				course.IsAllDownload = cursor.getInt(9);
				course.IsAudioDownload = cursor.getInt(10);
				course.IsVideoDownload = cursor.getInt(11);
				course.AllProgress = cursor.getFloat(12);
				course.AudioProgress = cursor.getFloat(13);
				course.VideoProgress = cursor.getFloat(14);
				course.totaltime = cursor.getString(15);
				courses.add(course);
			}
		} 
		catch (Exception e) {
		} 
		finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return courses;
		
		
	}
	
	
	//**********************************************************
	
	/**
	 * 查找  数据库里面的移动课堂中每套题中的课程数
	 * 
	 */
	public int findCourseContentSize(){
		
		int courseContentSize = 0;
		
		Cursor cursor = null;
		
		try {
			cursor = importDatabase.openDatabase().rawQuery(
					"select *" + " from " + TABLE_NAME_COURSECONTENT + " ORDER BY " + ID +" desc"
							, new String[] {});
			courseContentSize = cursor.getCount();
			cursor.close();
		} 
		catch (Exception e) {
		} 
		finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		Log.e("CourseContentSize:", courseContentSize+" ");		
		return courseContentSize;	
	}	
	
	/**
	 * 插入  数据库里面的移动课堂中每套题中每一节课的内容
	 * 
	 */
	public synchronized void insertCourseContent(List<CourseContent> courses){
		if (courses != null && courses.size() != 0) {
			String sqlString="insert into " + TABLE_NAME_COURSECONTENT + " (" + ID + ","
									+ TITLENAME + "," + COST + ","+ ISFREE + "," 
									+ ALLPROGRESS+ ","+ ISALLDOWNLOAD + ","+ AUDIOPROGRESS+ ","+ ISAUDIODOWNLOAD + ","
									+ VIDEOPROGRESS+ ","+ ISVIDEODOWNLOAD + ","+ PACKID + "," + LESSON + ","
									+ BTID+ ","+ BTNAME + ","+ VIDEO + ","+ TOTALTIME +") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			for (int i = 0; i < courses.size(); i++) {
				CourseContent course = courses.get(i);
				Object[] objects=new Object[]{ course.id,course.titleName,
						course.cost,course.IsFree,course.AllProgress,
						course.IsAllDownload,course.AudioProgress,
						course.IsAudioDownload,course.VideoProgress,
						course.IsVideoDownload,course.PackId,course.lesson,
						course.btid,course.btname,course.video,course.totaltime};
				importDatabase.openDatabase().execSQL(sqlString, objects);
				
			}
		}
	}	
	
	/**
	 * 插入  数据库里面的移动课堂中某套题中某一节课的内容(不带replace方式的插入)
	 * 
	 */
	public synchronized void insertSingleCourseContent(CourseContent course){
		if (course != null) {
			String sqlString="insert into " + TABLE_NAME_COURSECONTENT + " (" + ID + ","
									+ TITLENAME + "," + COST + ","+ ISFREE + "," 
									+ ALLPROGRESS+ ","+ ISALLDOWNLOAD + ","+ AUDIOPROGRESS+ ","+ ISAUDIODOWNLOAD + ","
									+ VIDEOPROGRESS+ ","+ ISVIDEODOWNLOAD + ","+ PACKID + "," + LESSON + ","
									+ BTID+ ","+ BTNAME + ","+ VIDEO + ","+ TOTALTIME +") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			Object[] objects=new Object[]{ course.id,course.titleName,
					course.cost,course.IsFree,course.AllProgress,
					course.IsAllDownload,course.AudioProgress,
					course.IsAudioDownload,course.VideoProgress,
					course.IsVideoDownload,course.PackId,course.lesson,
					course.btid,course.btname,course.video,course.totaltime};
			importDatabase.openDatabase().execSQL(sqlString, objects);
				
		}
	}	
	
	/**
	 * 插入  数据库里面的移动课堂中某套题中某一节课的内容
	 * 
	 */
	public synchronized void insertOrReplaceOneCourseContent(CourseContent course){
		if (course != null) {
			String sqlString="insert or replace into " + TABLE_NAME_COURSECONTENT + " (" + ID + ","
									+ TITLENAME + "," + COST + ","+ ISFREE + "," 
									+ ALLPROGRESS+ ","+ ISALLDOWNLOAD + ","+ AUDIOPROGRESS+ ","+ ISAUDIODOWNLOAD + ","
									+ VIDEOPROGRESS+ ","+ ISVIDEODOWNLOAD + ","+ PACKID + "," + LESSON + ","
									+ BTID+ ","+ BTNAME + ","+ VIDEO + ","+ TOTALTIME +") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			Object[] objects=new Object[]{ course.id,course.titleName,
					course.cost,course.IsFree,course.AllProgress,
					course.IsAllDownload,course.AudioProgress,
					course.IsAudioDownload,course.VideoProgress,
					course.IsVideoDownload,course.PackId,course.lesson,
					course.btid,course.btname,course.video,course.totaltime};
			importDatabase.openDatabase().execSQL(sqlString, objects);
				
		}
	}	
	
	
	
	/**
	 * 
	 * 查找移动课堂的所有课程包的内容信息
	 * @return
	 */
	public ArrayList<CourseContent> findCourseContentDataByAll() {
		ArrayList<CourseContent> courses = new ArrayList<CourseContent>();
//		mDB=getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = importDatabase.openDatabase().rawQuery(
					"select *" + " from " + TABLE_NAME_COURSECONTENT + " ORDER BY " + ID +" asc"
							, new String[] {});
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				CourseContent course = new CourseContent();
				course.id = cursor.getInt(0);
				course.titleName = cursor.getString(1);
				course.cost = cursor.getDouble(2);
				course.IsFree = Boolean.parseBoolean(cursor.getString(3));
				course.PackId = cursor.getInt(4);
				course.lesson = cursor.getString(5);
				course.btid = cursor.getInt(6);
				course.btname = cursor.getString(7);
				course.video = cursor.getInt(8);
				course.IsAllDownload = cursor.getInt(9);
				course.IsAudioDownload = cursor.getInt(10);
				course.IsVideoDownload = cursor.getInt(11);
				course.AllProgress = cursor.getFloat(12);
				course.AudioProgress = cursor.getFloat(13);
				course.VideoProgress = cursor.getFloat(14);
				course.totaltime = cursor.getString(15);
				courses.add(course);
			}
		} 
		catch (Exception e) {
		} 
		finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		Log.e("CourseContentSize ALL:", courses.size()+" ");		
		return courses;		
	}
	
	
	/**
	 * 
	 * 查找移动课堂的某个课程包的内容信息
	 * @return
	 */
	public ArrayList<CourseContent> findCourseContentDataBySpecial(String packId) {
		ArrayList<CourseContent> courses = new ArrayList<CourseContent>();
//		mDB=getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = importDatabase.openDatabase().rawQuery(
					"select *" + " from " + TABLE_NAME_COURSECONTENT + " where "+ PACKID + " = "+ packId + " ORDER BY " + ID +" asc"
							, new String[] {});
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				CourseContent course = new CourseContent();
				course.id = cursor.getInt(0);
				course.titleName = cursor.getString(1);
				course.cost = cursor.getDouble(2);
				course.IsFree = Boolean.parseBoolean(cursor.getString(3));
				course.PackId = cursor.getInt(4);
				course.lesson = cursor.getString(5);
				course.btid = cursor.getInt(6);
				course.btname = cursor.getString(7);
				course.video = cursor.getInt(8);
				course.IsAllDownload = cursor.getInt(9);
				course.IsAudioDownload = cursor.getInt(10);
				course.IsVideoDownload = cursor.getInt(11);
				course.AllProgress = cursor.getFloat(12);
				course.AudioProgress = cursor.getFloat(13);
				course.VideoProgress = cursor.getFloat(14);
				course.totaltime = cursor.getString(15);
				courses.add(course);
			}
		} 
		catch (Exception e) {
		} 
		finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		Log.e("CourseContentSize SEPCIAL:", courses.size()+" ");		
		return courses;		
	}
	
	/**
	 * 
	 * 查找移动课堂的某个课程包的内容信息
	 * @return
	 */
	public ArrayList<CourseContent> findDownloadCourseContentDataBySpecial(String packId) {
		ArrayList<CourseContent> courses = new ArrayList<CourseContent>();
//		mDB=getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = importDatabase.openDatabase().rawQuery(
					"select *" + " from " + TABLE_NAME_COURSECONTENT + " where "+ PACKID + " = "+ packId 
					+ " AND (IsAllDownload = 1 OR IsAudioDownload = 1) "+ " ORDER BY " + ID +" asc"
							, new String[] {});
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				CourseContent course = new CourseContent();
				course.id = cursor.getInt(0);
				course.titleName = cursor.getString(1);
				course.cost = cursor.getDouble(2);
				course.IsFree = Boolean.parseBoolean(cursor.getString(3));
				course.PackId = cursor.getInt(4);
				course.lesson = cursor.getString(5);
				course.btid = cursor.getInt(6);
				course.btname = cursor.getString(7);
				course.video = cursor.getInt(8);
				course.IsAllDownload = cursor.getInt(9);
				course.IsAudioDownload = cursor.getInt(10);
				course.IsVideoDownload = cursor.getInt(11);
				course.AllProgress = cursor.getFloat(12);
				course.AudioProgress = cursor.getFloat(13);
				course.VideoProgress = cursor.getFloat(14);
				course.totaltime = cursor.getString(15);
				courses.add(course);
			}
		} 
		catch (Exception e) {
		} 
		finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		Log.e("CourseContentSize SEPCIAL:", courses.size()+" ");		
		return courses;		
	}
	
//	public float findCourseContentProgressByClassName(String ClassName){
//		
//		float CourseContentProgress = 0;
//	
//		Cursor cursor = null;
//		try {
//			cursor = importDatabase.openDatabase().rawQuery(
//					"select Progress from " + TABLE_NAME_COURSECONTENT
//							+ " where titleName = ?" , new String[] {ClassName});
//			if(cursor.moveToFirst()){
//				
//				CourseContentProgress = cursor.getFloat(0);
//			}
//			cursor.close();
//		} 
//		catch (Exception e) {
//			if (cursor != null) {
//				cursor.close();
//				
//			}
//		} 
//		finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//		//Log.e("titleName:", ClassName+" ");	
//		//Log.e("CourseContentProgress:", CourseContentProgress+" ");		
//		return CourseContentProgress;	
//	}	
	
	/**
	 * 查找  数据库里面的移动课堂中某一节课的下载的进度
	 * 
	 */
//	public float findCourseContentProgress(String pos){
//		
//		float CourseContentProgress = 0;
//		
//		Cursor cursor = null;
//		try {
//			cursor = importDatabase.openDatabase().rawQuery(
//					"select Progress from " + TABLE_NAME_COURSECONTENT
//							+ " where titleName = ?" , new String[] {pos});
//			if(cursor.moveToFirst()){
//				
//				CourseContentProgress = cursor.getFloat(0);
//			}
//			cursor.close();
//		} 
//		catch (Exception e) {
//		} 
//		finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
////		Log.e("CourseContentProgress:", CourseContentProgress+" ");		
//		return CourseContentProgress;	
//	}	
	
	/**
	 * 更新  数据库里面的移动课堂中某套题中某一节课的内容
	 * 
	 */
	public synchronized void updateSingleCourseContent(CourseContent course){
		if (course != null) {
			String sqlString="insert or replace into " + TABLE_NAME_COURSECONTENT + " (" + ID + ","
									+ TITLENAME + "," + COST + ","+ ISFREE + "," 
									+ ALLPROGRESS+ ","+ ISALLDOWNLOAD + ","+ AUDIOPROGRESS+ ","+ ISAUDIODOWNLOAD + ","
									+ VIDEOPROGRESS+ ","+ ISVIDEODOWNLOAD + ","+ PACKID + "," + LESSON + ","
									+ BTID+ ","+ BTNAME + ","+ VIDEO + ","+ TOTALTIME +") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			Object[] objects=new Object[]{ course.id,course.titleName,
					course.cost,course.IsFree,course.AllProgress,
					course.IsAllDownload,course.AudioProgress,
					course.IsAudioDownload,course.VideoProgress,
					course.IsVideoDownload,course.PackId,course.lesson,
					course.btid,course.btname,course.video,course.totaltime};
			importDatabase.openDatabase().execSQL(sqlString, objects);
				
		}
	}	
	
	/**
	 * 
	 * 更新ContentCourse中的下载进度
	 * 
	 * @param packName
	 * @param progress
	 * @return
	 */
	public void setProgress(String titleId, float progress) {
		
		Log.d("更新当前进度：",progress+"");
		
		String sqlString = "update " + TABLE_NAME_COURSECONTENT
				+ " set Progress=" + progress
				+ " where id='" + titleId + "'";
		importDatabase.openDatabase().execSQL(sqlString);
	}
	
	
	public void setIsAllDownLoad(String titleId, int isdownload) {
		String sqlString = "update " + TABLE_NAME_COURSECONTENT
				+ " set IsAllDownLoad='"+isdownload+"', AllProgress=" + "1.0"
				+ " where id='" + titleId + "'";
		importDatabase.openDatabase().execSQL(sqlString);
	}
	
	public void setIsAudioDownLoad(String titleId, int isdownload) {
		String sqlString = "update " + TABLE_NAME_COURSECONTENT
				+ " set IsAudioDownLoad='"+isdownload+"', AudioProgress=" + "1.0"
				+ " where id='" + titleId + "'";
		importDatabase.openDatabase().execSQL(sqlString);
	}
	
	public void setIsVideoDownLoad(String titleId, int isdownload) {
		String sqlString = "update " + TABLE_NAME_COURSECONTENT
				+ " set IsVideoDownLoad='"+isdownload+"', VideoProgress=" + "1.0"
				+ " where id='" + titleId + "'";
		importDatabase.openDatabase().execSQL(sqlString);
	}
	
	/**
	 * 
	 * 更新ContentCourse中的购买详情
	 * 
	 * @param titleId
	 * @param isdownload
	 * @return
	 */
	public void setIsFree(String titleId, boolean isfree) {
		String sqlString = "update " + TABLE_NAME_COURSECONTENT
				+ " set IsFree='"+isfree+"'"
				+ " where id='" + titleId + "'";
		importDatabase.openDatabase().execSQL(sqlString);
	}
	
	public void setIsFreeForPack(String packId, boolean isfree) {
		String sqlString = "update " + TABLE_NAME_COURSECONTENT
				+ " set IsFree='"+isfree+"'"
				+ " where PackId='" + packId + "'";
		importDatabase.openDatabase().execSQL(sqlString);
	}
	
	public void setIsNotFree(String titleId, boolean isfree) {
		String sqlString = "update " + TABLE_NAME_COURSECONTENT
				+ " set IsFree='"+isfree+"'"
				+ " where id='" + titleId + "'";
		importDatabase.openDatabase().execSQL(sqlString);
	}
	
	public void setIsNotFreeForPack(String packId, boolean isfree) {
		String sqlString = "update " + TABLE_NAME_COURSECONTENT
				+ " set IsFree='"+isfree+"'"
				+ " where PackId='" + packId + "'";
		importDatabase.openDatabase().execSQL(sqlString);
	}
	
	/**
	 * 
	 * 更新ContentCourse中的下载,清空所有下载资源时，所有下载标志设为FALSE
	 *
	 * @param isdownload
	 * @return
	 */
//	public void updateIsDownload(boolean isdownload) {
//		String sqlString = "update " + TABLE_NAME_COURSECONTENT
//				+ " set IsDownLoad='"+isdownload+"', Progress=" + "0";
//		importDatabase.openDatabase().execSQL(sqlString);
//	}
	
	public void updateIsDownloadById(int isdownload,int courseid) {
		String sqlString = "update " + TABLE_NAME_COURSECONTENT
				+ " set IsAllDownload='"+isdownload+"', AllProgress=" + "0" 
				+ ", IsAudioDownload='"+isdownload+"', AudioProgress=" + "0" 
				+ ", IsVideoDownload='"+isdownload+"', VideoProgress=" + "0" 
				+ " where id = '" + courseid+"'";
		importDatabase.openDatabase().execSQL(sqlString);
	}
	
	/**
	 * 删除课程包中的每一小节课程的信息
	 * 
	 */
	
	public synchronized void deleteCourseContentDataById(int CourseContentId){
		String sqlString="delete from " + TABLE_NAME_COURSECONTENT
				+" where id ='"+CourseContentId+"'";
		importDatabase.openDatabase().execSQL(sqlString);
			
	}
	
	/**
	 * 删除课程包中的某一讲课程的信息
	 * 
	 */
	
	public synchronized void deleteCourseContentDataByPackId(int CourseContentPackId){
		String sqlString="delete from " + TABLE_NAME_COURSECONTENT
				+" where PackId ='"+CourseContentPackId+"'";
		importDatabase.openDatabase().execSQL(sqlString);
			
	}
	
	/**
	 * 删除课程包中的所有信息
	 * 
	 */
	
	public synchronized void deleteCourseContentData(){
		String sqlString="delete from " + TABLE_NAME_COURSECONTENT;
		importDatabase.openDatabase().execSQL(sqlString);
			
	}

}
