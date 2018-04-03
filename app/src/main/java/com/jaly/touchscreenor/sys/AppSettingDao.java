package com.jaly.touchscreenor.sys;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AppSettingDao {

	private DBHelper dbHelper; 

	public AppSettingDao(Context context) {
		dbHelper = new DBHelper(context);
	} 

	/**
	 * 更新一条记录
	 */
	public void set(AppSetting setting) {
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put("time_out", setting.getActivityTimeout());
		values.put("app_delay", setting.getAppDefDelay());
		values.put("app_type", setting.getAppListType());
		values.put("check_freq", setting.getCheckFreq());
		values.put("oper_delay", setting.getOperDefDelay());
		values.put("use_unicode", setting.isUnicode());
		if(get() == null){
			database.insert(DBHelper.TABLE_SETTING, null, values); 
		} else {
			database.update(DBHelper.TABLE_SETTING, values, "_id=1", null);			
		}
		database.close();
	}
	
	/**
	 * 查询单个记录
	 * @param id
	 * @return
	 */
	public AppSetting get(){
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		Cursor cursor = database.query(DBHelper.TABLE_SETTING, null, "_id=?",
				new String[]{"1"}, null, null, null);
		if(cursor.getCount() > 0){
			cursor.moveToNext(); 
			AppSetting setting = new AppSetting();
			setting.setAppListType(cursor.getInt(1));
			setting.setAppDefDelay(cursor.getInt(2));
			setting.setOperDefDelay(cursor.getInt(3));
			setting.setActivityTimeout(cursor.getInt(4));
			setting.setCheckFreq(cursor.getInt(5));
			setting.setUnicode(cursor.getInt(6) != 0);
			return setting;
		} 
		return null;
	} 

}
