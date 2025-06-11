package com.iyuba.core.common.sqlite.op;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.iyuba.core.common.sqlite.db.DatabaseService;

public abstract class BaseOP extends DatabaseService {

	public BaseOP(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		createConfigTable();
	}

	/**
	 * 鍒涘缓琛�?
	 */
	public void createConfigTable() {
		importDatabase.openDatabase().execSQL(setCreateTabSql());
	}

	public abstract String setCreateTabSql();

	public SQLiteDatabase getDatabase() {
		return importDatabase.openDatabase();
	}

}
