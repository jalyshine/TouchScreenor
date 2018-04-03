package com.jaly.touchscreenor.sys;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class DBHelper extends SQLiteOpenHelper {

	public static final String TABLE_TASK_TIEM = "task_item";
	public static final String TABLE_SETTING = "setting";

	public DBHelper(Context context) {
		super(context, "sys.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建任务表
		db.execSQL("create table " + TABLE_TASK_TIEM
				+ "(_id integer primary key autoincrement, "
				+ "file varchar, start int, param varchar, running int)");
		// 创建系统设置表
		db.execSQL("create table " + TABLE_SETTING
				+ "(_id integer primary key autoincrement, "
				+ "app_type int, app_delay int, oper_delay int,"
				+ " time_out int, check_freq int, use_unicode int)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
