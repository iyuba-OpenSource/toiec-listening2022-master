/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.core.common.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.core.common.sqlite.db.DatabaseService;
import com.iyuba.core.common.sqlite.mode.Sayings;

/**
 * 类名
 * 
 * @author 作者 <br/>
 *         实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class SayingsOp extends DatabaseService {
	public static final String TABLE_NAME = "sayings";
	public static final String ID = "id";
	public static final String ENGLISH = "english";
	public static final String CHINESE = "chinese";

	public SayingsOp(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		// CreateTabSql();
	}

	public void CreateTabSql() {
		// TODO Auto-generated method stub
		closeDatabase(null);
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public synchronized Sayings findDataById(int id) {
		Sayings sayings = new Sayings();
		Cursor cursor = importDatabase.openDatabase().rawQuery(
				"select " + ID + "," + ENGLISH + ", " + CHINESE + " from "
						+ TABLE_NAME + " where " + ID + "=? ",
				new String[] { String.valueOf(id) });
		if (cursor.moveToNext()) {
			sayings.id = cursor.getInt(0);
			sayings.english = cursor.getString(1);
			sayings.chinese = cursor.getString(2);
			closeDatabase(null);
			return sayings;
		}
		closeDatabase(null);
		return null;
	}
}
