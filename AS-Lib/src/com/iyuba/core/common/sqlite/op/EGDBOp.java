package com.iyuba.core.common.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.core.common.sqlite.db.DatabaseService;

/**
 * 获取单词表数据库
 * 
 * @author ct
 * @time 12.9.27
 * 
 */
public class EGDBOp extends DatabaseService {
	public static final String TABLE_NAME_WORD = "example";
	public static final String WORD = "word";
	public static final String ORIG = "orig";
	public static final String TRANS = "trans";

	public EGDBOp(Context context) {
		super(context);
	}

	public synchronized String findData(String word) {
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + WORD + "," + ORIG + "," + TRANS + " from "
						+ TABLE_NAME_WORD + " WHERE " + WORD + "=?",
				new String[] { word });
		StringBuffer sb = new StringBuffer();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			sb.append(cursor.getPosition() + 1).append(": ")
					.append(cursor.getString(1)).append("<br/>")
					.append(cursor.getString(2)).append("<br/><br/>");
		}
		if (cursor != null) {
			cursor.close();
		}
		closeDatabase(null);
		try {
			return sb.toString();
		} catch (Exception e) {
			return "暂无";
		}
	}
}
