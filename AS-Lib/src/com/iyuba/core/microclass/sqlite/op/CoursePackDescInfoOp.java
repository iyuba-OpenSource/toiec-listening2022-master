package com.iyuba.core.microclass.sqlite.op;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.iyuba.core.common.sqlite.db.DatabaseService;
import com.iyuba.core.microclass.sqlite.mode.CoursePackDescInfo;

public class CoursePackDescInfoOp extends DatabaseService {

	//CoursePackDescInfo表
	public static final String TABLE_NAME_COURSEPACKDESCINFO = "CoursePackDescInfo";
	public static final String ID = "id";
	public static final String DETAIL = "detail";
	public static final String CONDITION = "condition";
	public static final String TID = "tid";
	public static final String RECOMMENDID = "recommendId";
	public static final String VIEWCOUNT = "viewCount";
	public static final String QQ = "qq";
	
	public CoursePackDescInfoOp(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public synchronized void insertCoursePackDescs(CoursePackDescInfo cpdInfo){
		if (cpdInfo != null ) {
			String sqlString="insert or replace into " + TABLE_NAME_COURSEPACKDESCINFO + " (" + ID + ","
					+ DETAIL + "," + CONDITION + "," + TID + ","
					+ RECOMMENDID + ","+ VIEWCOUNT + ","+ QQ +") values(?,?,?,?,?,?,?)";
			
			Object[] objects=new Object[]{ cpdInfo.id, cpdInfo.detail, cpdInfo.condition,
					cpdInfo.tid, cpdInfo.recommendId,cpdInfo.viewCount,cpdInfo.qq};
			importDatabase.openDatabase().execSQL(sqlString,objects);
			
			closeDatabase(null);
		}
	}	
	
	/**
	 * 删除  数据库里面的资讯
	 * 
	 */
	
	public synchronized boolean deleteCoursePackDescInfoData(){
		String sqlString="delete from CoursePackDescInfo";
		try{
			importDatabase.openDatabase().execSQL(sqlString);
			
			closeDatabase(null);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false; 
		}
		
	}
	
	/**
	 * 删除  数据库里面课程包描述信息的数据表
	 * 
	 */
	
	public synchronized boolean deleteCoursePackDescInfoDataById(int packId){
		String sqlString="delete from CoursePackDescInfo"
				+ " where id ='"+ packId+"'";
		try{
			importDatabase.openDatabase().execSQL(sqlString);
			
			closeDatabase(null);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false; 
		}
		
	}
	
	
	/**
	 * 
	 * 查找所有的移动课堂的课程包的描述信息
	 * @return
	 */
	public synchronized ArrayList<CoursePackDescInfo> findDataByAll() {
		ArrayList<CoursePackDescInfo> cpdInfos = new ArrayList<CoursePackDescInfo>();
	
		Cursor cursor = null;
		try {
			cursor = importDatabase.openDatabase().rawQuery(
					"select *" + " from " + TABLE_NAME_COURSEPACKDESCINFO
							, new String[] {});
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				CoursePackDescInfo cpd = new CoursePackDescInfo();
				cpd.id = cursor.getInt(0);
				cpd.detail = cursor.getString(1);
				cpd.condition = cursor.getString(2);
				cpd.tid = cursor.getInt(3);
				cpd.recommendId = cursor.getInt(4);
				cpd.viewCount = cursor.getInt(5);
				cpd.qq = cursor.getString(6);
				cpdInfos.add(cpd);
			}
			cursor.close();
		} 
		catch (Exception e) {
		} 
		finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		Log.e("CoursePackDescInfo:", cpdInfos.size()+" ");		
		return cpdInfos;		
	}
	
	
	/**
	 * 
	 * 查找所有的移动课堂的课程包的描述信息
	 * @return
	 */
	public synchronized CoursePackDescInfo findDataByOwnerId(String packId) {
		CoursePackDescInfo cpd = null;
	
		Cursor cursor = null;
		try {
			cursor = importDatabase.openDatabase().rawQuery(
					"select *" + " from " + TABLE_NAME_COURSEPACKDESCINFO + " where " + ID + "=" +packId
							, new String[] {});
			if (cursor.moveToFirst()) {
				cpd = new CoursePackDescInfo();
				cpd.id = cursor.getInt(0);
				cpd.detail = cursor.getString(1);
				cpd.condition = cursor.getString(2);
				cpd.tid = cursor.getInt(3);
				cpd.recommendId = cursor.getInt(4);
				cpd.viewCount = cursor.getInt(5);
				cpd.qq = cursor.getString(6);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
		finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return cpd;		
	}
	/*
	 * 更新课程包描述信息表的detail字段
	 * */
	public void setDetailForPackDesc(String packId, String detail) {
		String sqlString = "update " + TABLE_NAME_COURSEPACKDESCINFO
				+ " set detail='"+detail+"'"
				+ " where id='" + packId + "'";
		importDatabase.openDatabase().execSQL(sqlString);
	}
	
	/*
	 * 更新课程包描述信息表的condition字段
	 * */
	public void setConditionForPackDesc(String packId, String condition) {
		String sqlString = "update " + TABLE_NAME_COURSEPACKDESCINFO
				+ " set condition='"+condition+"'"
				+ " where id='" + packId + "'";
		importDatabase.openDatabase().execSQL(sqlString);
	}
	
	/*
	 * 更新课程包描述信息表的tid字段
	 * */
	public void setTidForPackDesc(String packId, String tid) {
		String sqlString = "update " + TABLE_NAME_COURSEPACKDESCINFO
				+ " set tid='"+tid+"'"
				+ " where id='" + packId + "'";
		importDatabase.openDatabase().execSQL(sqlString);
	}
	
	/*
	 * 更新课程包描述信息表的recommendId字段
	 * */
	public void setRecommendIdForPackDesc(String packId, String recommendId) {
		String sqlString = "update " + TABLE_NAME_COURSEPACKDESCINFO
				+ " set recommendId='"+recommendId+"'"
				+ " where id='" + packId + "'";
		importDatabase.openDatabase().execSQL(sqlString);
	}
	
	/*
	 * 更新课程包描述信息表的viewCount字段
	 * */
	public void setViewCountForPackDesc(String packId, String viewCount) {
		String sqlString = "update " + TABLE_NAME_COURSEPACKDESCINFO
				+ " set viewCount='"+viewCount+"'"
				+ " where id='" + packId + "'";
		importDatabase.openDatabase().execSQL(sqlString);
	}

}
