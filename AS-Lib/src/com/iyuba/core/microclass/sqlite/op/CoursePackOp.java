package com.iyuba.core.microclass.sqlite.op;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.iyuba.core.common.sqlite.db.DatabaseService;
import com.iyuba.core.iyumooc.microclass.bean.CoursePackListBean;
import com.iyuba.core.microclass.sqlite.mode.CoursePack;

public class CoursePackOp extends DatabaseService {

	//CoursePack表
	public static final String TABLE_NAME_COURSEPACK = "CoursePack";
	public static final String ID = "id";
	public static final String PRICE = "price";
	public static final String DESC = "desc";
	public static final String NAME = "name";
	public static final String OWNERID = "ownerid";
	public static final String PICURL = "picUrl";
	public static final String CLASSNUM = "classNum";
	
	public static final String REALPRICE = "realprice";
	public static final String VIEWCOUNT = "viewCount";
	
	
	public CoursePackOp(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public synchronized void insertCoursePacks(List<CoursePack> courses){
		if (courses != null && courses.size() != 0) {
			String sqlString="insert or replace into " + TABLE_NAME_COURSEPACK + " (" + ID + ","
					+ PRICE + "," + DESC + "," + NAME + ","
					+ OWNERID + ","+ PICURL + "," + CLASSNUM + ","
					+ REALPRICE + "," + VIEWCOUNT +") values(?,?,?,?,?,?,?,?,?)";
			
			for (int i = 0; i < courses.size(); i++) {
				CoursePack course = courses.get(i);
				Object[] objects=new Object[]{ course.id, course.price, course.desc,
						course.name, course.ownerid,course.picUrl,course.classNum,
						course.realprice,course.viewCount};
				importDatabase.openDatabase().execSQL(sqlString,objects);
				
				closeDatabase(null);
			}
		}
	}	
	
	/**
	 * 删除  数据库里面的资讯
	 * 
	 */
	
	public synchronized boolean deleteCoursePackData(){
		String sqlString="delete from CoursePack";
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
	 * 查找所有的移动课堂的课程包的信息
	 * @return
	 */
	public synchronized ArrayList<CoursePack> findDataByAll() {
		ArrayList<CoursePack> courses = new ArrayList<>();
	
		Cursor cursor = null;
		try {
			cursor = importDatabase.openDatabase().rawQuery(
//					"select *" + " from " + TABLE_NAME_COURSEPACK + " ORDER BY " + OWNERID + " asc"+","+ ID +" desc"
//					"select *" + " from " + TABLE_NAME_COURSEPACK + " ORDER BY ownerid,id"
					"select *" + " from " + TABLE_NAME_COURSEPACK
							, new String[] {});
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				CoursePack course = new CoursePack();
				course.id=cursor.getInt(0);
				course.price= Double.parseDouble(cursor.getString(1));
				course.desc=(cursor.getString(2));
				course.name=(cursor.getString(3));
				course.ownerid= Integer.parseInt((cursor.getString(4)));
				course.picUrl=(cursor.getString(5));
				course.classNum=(cursor.getInt(6));
				course.realprice= Double.parseDouble((cursor.getString(7)));
				course.viewCount=(cursor.getInt(8));
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
		Log.e("CoursePackSize:", courses.size()+" ");		
		return courses;		
	}
	
	
	/**
	 * 
	 * 查找所有的移动课堂的课程包的信息
	 * @return
	 */
	public synchronized ArrayList<CoursePack> findDataByOwnerId(String ownerId) {
		ArrayList<CoursePack> courses = new ArrayList<>();

		Cursor cursor = null;
		try {
			cursor = importDatabase.openDatabase().rawQuery(
//					"select *" + " from " + TABLE_NAME_COURSEPACK + " ORDER BY " + OWNERID + " asc"+","+ ID +" desc"
					"select *" + " from " + TABLE_NAME_COURSEPACK + " where " + OWNERID + "=" +ownerId
					, new String[] {});
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				CoursePack course = new CoursePack();
				course.id=(cursor.getInt(0));
				course.price= Double.parseDouble((cursor.getString(1)));
				course.desc=(cursor.getString(2));
				course.name=(cursor.getString(3));
				course.ownerid= Integer.parseInt((cursor.getString(4)));
				course.picUrl=(cursor.getString(5));
				course.classNum=(cursor.getInt(6));
				course.realprice= Double.parseDouble((cursor.getString(7)));
				course.viewCount=(cursor.getInt(8));
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
//		Log.e("CoursePackSizeByOwnerId:", courses.size()+" ");
		return courses;
	}
	
	/**
	 * 查询数据分页
	 * 
	 * @return
	 */
	public synchronized ArrayList<CoursePackListBean.CoursePackDataBean> findDataByPage(int count, int offset) {
		
		ArrayList<CoursePackListBean.CoursePackDataBean> courses = new ArrayList<>();
		
		Cursor cursor = null;
		try {
			cursor = importDatabase.openDatabase().rawQuery(
//					"select *" + " from " + TABLE_NAME_COURSEPACK + " ORDER BY " + OWNERID + " asc"+","+ ID +" desc"
					"select *" + " from " + TABLE_NAME_COURSEPACK + " Limit " + count + " Offset " + offset
							, new String[] {});
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				CoursePackListBean.CoursePackDataBean course = new CoursePackListBean.CoursePackDataBean();
				course.setId(cursor.getInt(0));
				course.setPrice(cursor.getString(1));
				course.setDesc(cursor.getString(2));
				course.setName(cursor.getString(3));
				course.setOwnerid(cursor.getString(4));
				course.setPic(cursor.getString(5));
				course.setClassNum(cursor.getInt(6));
				course.setRealprice(cursor.getString(7));
				course.setViewCount(cursor.getInt(8));
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
		Log.e("CoursePackPageSize:", courses.size()+" ");		
		return courses;		
		
	}

}
