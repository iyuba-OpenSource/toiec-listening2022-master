package com.iyuba.core.microclass.sqlite.op;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.iyuba.core.common.sqlite.db.DatabaseService;
import com.iyuba.core.microclass.sqlite.mode.TeacherInfo;

public class TeacherInfoOp extends DatabaseService {

	//Teacher表
	public static final String TABLE_NAME_TEACHERINFO = "TeacherInfo";
	public static final String TID = "tid";
	public static final String TIMG = "timg";
	public static final String TDES = "tdes";
	public static final String TNAME = "tname";
	
	
	public TeacherInfoOp(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public synchronized void insertTeachers(TeacherInfo teacherInfo){
		if (teacherInfo != null) {
			String sqlString="insert or replace into " + TABLE_NAME_TEACHERINFO + " (" + TID + ","
					+ TNAME + "," + TDES + "," + TIMG +") values(?,?,?,?)";
			
			Object[] objects=new Object[]{ teacherInfo.tid, teacherInfo.tname, 
					teacherInfo.tdes,teacherInfo.timg};
			importDatabase.openDatabase().execSQL(sqlString,objects);
			
			closeDatabase(null);
		}
	}	
	
	/**
	 * 
	 * 查找所有的教师信息
	 * @return
	 */
	public synchronized ArrayList<TeacherInfo> findDataByAll() {
		ArrayList<TeacherInfo> teachers = new ArrayList<TeacherInfo>();
	
		Cursor cursor = null;
		try {
			cursor = importDatabase.openDatabase().rawQuery(
					"select *" + " from " + TABLE_NAME_TEACHERINFO
							, new String[] {});
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				TeacherInfo teacher  = new TeacherInfo();
				teacher.tid = cursor.getInt(0);
				teacher.tname = cursor.getString(1);
				teacher.timg = cursor.getString(2);
				teacher.tdes = cursor.getString(3);
				teachers.add(teacher);
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
		Log.e("TeacherInfoSize:", teachers.size()+" ");		
		return teachers;		
	}
	
	
	/**
	 * 
	 * 查找特定的教师的信息
	 * @return
	 */
	public synchronized TeacherInfo findDataByOwnerId(String teacherId) {
	
		Cursor cursor = null;
		TeacherInfo teacher  = new TeacherInfo();
		try {
			cursor = importDatabase.openDatabase().rawQuery(
					"select *" + " from " + TABLE_NAME_TEACHERINFO + " where " + TID + "=" +teacherId
							, new String[] {});
			
			if (cursor.moveToFirst()) {
				teacher.tid = cursor.getInt(0);
				teacher.tname = cursor.getString(1);
				teacher.timg = cursor.getString(2);
				teacher.tdes = cursor.getString(3);
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
		return teacher;		
	}

}
