package com.iyuba.core.microclass.sqlite.op;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.iyuba.core.common.sqlite.db.DatabaseService;
import com.iyuba.core.microclass.sqlite.mode.MbText;

public class MobClassResOp extends DatabaseService {
	
	//MobClassRes表
	public static final String TABLE_NAME_MOBCLASSRES = "MobClassRes";
	public static final String ID = "id";
	public static final String IMAGENAME = "imageName";
	public static final String SECONDS = "seconds";
	public static final String ANSWER = "answer";
	public static final String NUMBER = "number";
	public static final String TYPE = "type";
	public static final String TITLEID = "TitleId";
	public static final String PACKID = "PackId";

	public MobClassResOp(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 查找  数据库里面的移动课堂中某一节课的资源信息数目
	 * 
	 */
	public synchronized int findMbTextSize(String titleId){
		
		int MbTextSize = 0;
		
		Cursor cursor = null;
		try {
			cursor = importDatabase.openDatabase().rawQuery(
					"select *" + " from " + TABLE_NAME_MOBCLASSRES + " where TitleId =" + titleId+ " ORDER BY " + ID +" desc"
							, new String[] {});
			MbTextSize = cursor.getCount();
			cursor.close();
		} 
		catch (Exception e) {
		} 
		finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		Log.e("MbTextSize:", MbTextSize+" ");		
		return MbTextSize;	
	}	
	
	/**
	 * 查找  数据库里面的移动课堂中某一节课的资源信息数目
	 * 
	 */
	public synchronized int findMbTextSize(){
		
		int MbTextSize = 0;
		
		Cursor cursor = null;
		try {
			cursor = importDatabase.openDatabase().rawQuery(
					"select *" + " from " + TABLE_NAME_MOBCLASSRES + " ORDER BY " + ID +" desc"
							, new String[] {});
			MbTextSize = cursor.getCount();
			cursor.close();
		} 
		catch (Exception e) {
		} 
		finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		Log.e("MbTextSize:", MbTextSize+" ");		
		return MbTextSize;	
	}	
	
	/**
	 * 插入  数据库里面的移动课堂中某一节课的资源信息
	 * 
	 */
	public synchronized void insertMbText(List<MbText> mbTexts){
		if (mbTexts != null && mbTexts.size() != 0) {
			String sqlString="insert or replace into " + TABLE_NAME_MOBCLASSRES + " (" + ID + ","
									+ SECONDS + "," + IMAGENAME + "," + ANSWER 
									+ "," + NUMBER + "," + TYPE
									+ "," + TITLEID + "," + PACKID
									+ ") values(?,?,?,?,?,?,?,?)";
			for (int i = 0; i < mbTexts.size(); i++) {
				MbText mbText = mbTexts.get(i);
				Object[] objects=new Object[]{ mbText.id, mbText.seconds, mbText.imageName,
						mbText.answer, mbText.number,mbText.type,mbText.TitleId,mbText.PackId};
				importDatabase.openDatabase().execSQL(sqlString, objects);
				
				Log.d("MbText内容的插入执行id",mbText.id+"");
			}
		}
	}	
	
	/**
	 * 
	 * 查找移动课堂的某个课程包中某节课的资源信息
	 * @return
	 */
	public synchronized ArrayList<MbText> findSpecialCourseResourceData(String titleId) {
		ArrayList<MbText> mbTexts = new ArrayList<MbText>();
		Cursor cursor = null;
		try {
			cursor = importDatabase.openDatabase().rawQuery(
					"select *" + " from " + TABLE_NAME_MOBCLASSRES + " where TitleId =" + titleId 
						+ " ORDER BY " + ID +" asc", new String[] {});
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				MbText mbtext = new MbText();
				mbtext.id = cursor.getInt(0);
				mbtext.imageName = cursor.getString(1);
				mbtext.seconds = cursor.getInt(2);
				mbtext.answer = cursor.getInt(3);
				mbtext.number = cursor.getInt(4);
				mbtext.type = cursor.getInt(5);
				mbtext.TitleId = cursor.getInt(6);
				mbtext.PackId = cursor.getInt(7);
				mbTexts.add(mbtext);
			}
		} 
		catch (Exception e) {
		} 
		finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		Log.e("SpecialMbTextSize:", mbTexts.size()+" ");		
		return mbTexts;		
	}
	
	/**
	 * 
	 * 查找移动课堂的某个课程包中某节课的资源信息
	 * @return
	 */
	public synchronized ArrayList<MbText> findSpecialCourseResourceData(String titleId,String PackId) {
		ArrayList<MbText> mbTexts = new ArrayList<MbText>();
//		mDB=getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = importDatabase.openDatabase().rawQuery(
					"select *" + " from " + TABLE_NAME_MOBCLASSRES + " where TitleId =" + titleId +" and PackId = "+ PackId
						+ " ORDER BY " + ID +" asc", new String[] {});
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				MbText mbtext = new MbText();
				mbtext.id = cursor.getInt(0);
				mbtext.imageName = cursor.getString(1);
				mbtext.seconds = cursor.getInt(2);
				mbtext.answer = cursor.getInt(3);
				mbtext.number = cursor.getInt(4);
				mbtext.type = cursor.getInt(5);
				mbtext.TitleId = cursor.getInt(6);
				mbtext.PackId = cursor.getInt(7);
				mbTexts.add(mbtext);
			}
		} 
		catch (Exception e) {
		} 
		finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		Log.e("SpecialMbTextSize:", mbTexts.size()+" ");		
		return mbTexts;		
	}
	
	/**
	 * 
	 * 查找移动课堂的某个课程包的资源信息
	 * @return
	 */
	public synchronized ArrayList<MbText> findCourseResourceDataByAll() {
		ArrayList<MbText> mbTexts = new ArrayList<MbText>();
		Cursor cursor = null;
		try {
			cursor = importDatabase.openDatabase().rawQuery(
					"select *" + " from " + TABLE_NAME_MOBCLASSRES + " ORDER BY " + ID +" desc"
							, new String[] {});
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				MbText mbtext = new MbText();
				mbtext.id = cursor.getInt(0);
				mbtext.imageName = cursor.getString(1);
				mbtext.seconds = cursor.getInt(2);
				mbtext.answer = cursor.getInt(3);
				mbtext.number = cursor.getInt(4);
				mbtext.type = cursor.getInt(5);
				mbtext.TitleId = cursor.getInt(6);
				mbtext.PackId = cursor.getInt(7);
				mbTexts.add(mbtext);
			}
		} 
		catch (Exception e) {
		} 
		finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		Log.e("MbTextSize:", mbTexts.size()+" ");		
		return mbTexts;		
	}

}
