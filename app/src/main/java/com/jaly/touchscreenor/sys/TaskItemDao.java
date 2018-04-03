package com.jaly.touchscreenor.sys;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TaskItemDao {

	private DBHelper dbHelper;

	public TaskItemDao(Context context) {
		dbHelper = new DBHelper(context);
	}

	/**
	 * 添加一条记录
	 * 
	 * @param blackNumber
	 */
	public void add(TaskItem taskItem) {
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put("file", taskItem.getFileName());
		values.put("start", taskItem.getlStartTime());
		values.put("param", taskItem.getParamString());
		values.put("running", taskItem.isRunning());
		long id = database.insert(DBHelper.TABLE_TASK_TIEM, null, values);
		taskItem.setId((int) id);
		database.close();
	}

	/**
	 * 根据id删除一条记录
	 */
	public void delete(int id) {
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		database.delete(DBHelper.TABLE_TASK_TIEM, "_id=?", new String[] { id
				+ "" });
		database.close();
	}

	/**
	 * 更新一条记录
	 */
	public void update(TaskItem taskItem) {
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put("file", taskItem.getFileName());
		values.put("start", taskItem.getlStartTime());
		values.put("param", taskItem.getParamString());
		values.put("running", taskItem.isRunning());
		database.update(DBHelper.TABLE_TASK_TIEM, values, "_id=" + taskItem.getId(), null);
		database.close();
	}
	
	/**
	 * 查询单个记录
	 * @param id
	 * @return
	 */
	public TaskItem get(Integer id){
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		Cursor cursor = database.query(DBHelper.TABLE_TASK_TIEM, null, "_id=?",
				new String[]{id.toString()}, null, null, null);
		if(cursor.getCount() > 0){
			cursor.moveToNext(); 
			String fileName = cursor.getString(1);
			Long startTime = (long) cursor.getInt(2);
			String paraString = cursor.getString(3);
			Boolean running = (cursor.getInt(4) != 0);
			return new TaskItem(id, fileName, startTime, paraString, running);
		} else {
			return null;
		} 
	}

	/**
	 * 查询所有记录封装成List<BLackNumber>
	 */
	public List<TaskItem> getAll() {
		List<TaskItem> list = new ArrayList<TaskItem>();
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		Cursor cursor = database.query(DBHelper.TABLE_TASK_TIEM, null, null,
				null, null, null, "_id desc");
		while (cursor.moveToNext()) {
			int id = cursor.getInt(0);
			String fileName = cursor.getString(1);
			Long startTime = (long) cursor.getInt(2);
			String paraString = cursor.getString(3);
			Boolean running = (cursor.getInt(4) != 0); 
			list.add(new TaskItem(id, fileName, startTime, paraString, running));
		}
		cursor.close();
		database.close();
		return list;
	}

}
