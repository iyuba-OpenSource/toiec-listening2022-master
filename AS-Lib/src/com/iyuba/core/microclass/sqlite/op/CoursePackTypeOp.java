package com.iyuba.core.microclass.sqlite.op;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.iyuba.core.common.sqlite.db.DatabaseService;
import com.iyuba.core.iyumooc.microclass.bean.CourseTypeListBean;
import com.iyuba.core.microclass.sqlite.mode.CoursePackType;

public class CoursePackTypeOp extends DatabaseService {
	//CoursePackType表
	public static final String TABLE_NAME_COURSEPACKTYPE = "CoursePackType";
	public static final String ID = "id";
	public static final String DESC = "desc";
	public static final String CONDITION = "condition";
	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String DESTINATION = "destination";

	public CoursePackTypeOp(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public synchronized void insertCoursePackType(List<CoursePackType> courses){
		if (courses != null && courses.size() != 0) {
			String sqlString="insert into " + TABLE_NAME_COURSEPACKTYPE + " (" + ID + ","
					+ DESC + "," + CONDITION + "," + NAME + ","
					+ TYPE + ","+ DESTINATION + ") values(?,?,?,?,?,?)";
			
			for (int i = 0; i < courses.size(); i++) {
				CoursePackType course = courses.get(i);
				Object[] objects=new Object[]{ course.id, course.desc, course.condition,
						course.name, course.type,course.destination};
				importDatabase.openDatabase().execSQL(sqlString,objects);
				
				closeDatabase(null);
			}
		}
	}	
	
	/**
	 * 删除  数据库里面的课程包分类数据
	 * 
	 */
	
	public synchronized boolean deleteCoursePackTypeData(){
		String sqlString="delete from CoursePackType";
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
	 * 查找所有的移动课堂的课程包的分类信息
	 * @return
	 */
	public synchronized ArrayList<CoursePackType> findDataByAll() {
		ArrayList<CoursePackType> courses = new ArrayList<>();

		Cursor cursor = null;
		try {
			cursor = importDatabase.openDatabase().rawQuery(
//					"select *" + " from " + TABLE_NAME_COURSEPACKTYPE + " ORDER BY " + OWNERID + " asc"+","+ ID +" desc"
					"select *" + " from " + TABLE_NAME_COURSEPACKTYPE
					, new String[] {});
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				CoursePackType course = new CoursePackType();
				course.id=(cursor.getInt(0));
				course.desc=(cursor.getString(1));
				course.condition=(cursor.getString(2));
				course.name=(cursor.getString(3));
				course.type=(cursor.getInt(4));
				course.destination=(cursor.getString(5));
				courses.add(course);
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
		Log.e("CoursePackTypeSize:", courses.size()+" ");
		return courses;
	}

}
