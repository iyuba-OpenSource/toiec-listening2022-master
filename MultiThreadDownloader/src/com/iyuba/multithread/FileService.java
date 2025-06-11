package com.iyuba.multithread;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FileService {
	private DBOpenHelper openHelper;

	public FileService(Context context) {
		openHelper = new DBOpenHelper(context);
	}
	
	/**
	 * ????????????????????????
	 * @param path
	 * @return
	 */
	public synchronized Map<Integer, Integer> getData(String path){
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select threadid, downlength from filedownlog where downurl=?", new String[]{path});
		Map<Integer, Integer> data = new HashMap<Integer, Integer>();
		
		while(cursor.moveToNext()){
			data.put(cursor.getInt(0), cursor.getInt(1));
		}
		
		cursor.close();
		db.close();
		return data;
	}
	
	public synchronized boolean isInDownloadDB(String path){
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select threadid, downlength from filedownlog where downurl=?", new String[]{path});
		if (cursor.getCount()>0) {
			cursor.close();
			db.close();
			return true;
		}
		cursor.close();
		db.close();
			return false;
	}
	
	/**
	 * ?????????????????????????
	 * @param path
	 * @param map
	 */
	public synchronized void save(String path,  Map<Integer, Integer> map){//int threadid, int position
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.beginTransaction();
		
		try{
			for(Map.Entry<Integer, Integer> entry : map.entrySet()){
				db.execSQL("insert into filedownlog(downurl, threadid, downlength) values(?,?,?)",
						new Object[]{path, entry.getKey(), entry.getValue()});
			}
			
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
		}
		
		db.close();
	}
	
	/**
	 * ?????????????????????
	 * @param path
	 * @param map
	 */
	public synchronized void saveFileSize(String path,int filesize){//int threadid, int position
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.execSQL("update filedownlog set titallength="+filesize+" where downurl='"+path+"'");
		db.close();
	}
	
	/**
	 * ??????????????ID????voaid)
	 * @param path
	 * @param map
	 */
	public synchronized void saveFileId(String url,int id){//int threadid, int position
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.execSQL("update filedownlog set fileid="+id+" where downurl='"+url+"'");
		db.close();
	}
	
	/**
	 * ?????????????????????
	 * @param path
	 * @param map
	 */
	public synchronized void saveFilePath(String path,String url){//int threadid, int position
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.execSQL("update filedownlog set downpath='"+path+"' where downurl='"+url+"'");
		db.close();
	}
	
	/**
	 * ???????????????????????????
	 * @param path
	 * @param map
	 */
	public synchronized void update(String path, Map<Integer, Integer> map){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.beginTransaction();
		
		try{
			for(Map.Entry<Integer, Integer> entry : map.entrySet()){
				db.execSQL("update filedownlog set downlength=? where downurl=? and threadid=?",
						new Object[]{entry.getValue(), path, entry.getKey()});
			}
			
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
		}
		
		db.close();
	}
	
	/**
	 * ???????????????????????????
	 * @param path
	 */
	public synchronized void delete(String path){
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.execSQL("delete from filedownlog where downurl=?", new Object[]{path});
		db.close();
	}
	
	/**
	 * 
	 * @param path
	 */
	public synchronized HashMap<Integer,FileDownloader> findDownload(Context context){
		HashMap<Integer,FileDownloader> tasks = new HashMap<Integer, FileDownloader>();
		SQLiteDatabase db = openHelper.getWritableDatabase();
		Cursor cursor = db.query("filedownlog", new String[]{"fileid","downurl","downpath","SUM(downlength)","titallength"},null, null,"fileid", null, "fileid DESC");
			for (cursor.moveToFirst();!cursor.isAfterLast(); cursor.moveToNext()) {
				FileDownloader  tempDownloadTask = new FileDownloader(
						context, cursor.getString(1),
						new File(cursor.getString(2)),
						2);
				tempDownloadTask.setTaskid(cursor.getInt(0));
				tempDownloadTask.setFileSize(cursor.getInt(4));
				tempDownloadTask.setDownloadSize(cursor.getInt(3));
				tasks.put(tempDownloadTask.taskid, tempDownloadTask);
			}
			cursor.close();
			db.close();
			return tasks;
		}
	
	public static class DownloadTask{
		public DownloadTask() {
			super();
			this.voaid = -1;
			this.url = "";
			this.downloadedBytes = 0;
			this.totalBytes = 0;
			this.downloadPercentage = 0;
			this.savePath = "";
		}
		// ???
		public int voaid;
		// ??????????
		public String url;
		// ?????????????????
		public long downloadedBytes;
		// ??????????
		public long totalBytes;
		// ???????
		public int downloadPercentage;
		// ????????
		public String savePath;
	}
}
