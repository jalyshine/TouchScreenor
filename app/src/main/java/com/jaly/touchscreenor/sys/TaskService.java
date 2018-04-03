package com.jaly.touchscreenor.sys;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.jaly.touchscreenor.MainActivity;
import com.jaly.touchscreenor.coding.ScriptManager;
import com.jaly.touchscreenor.coding.TagScript;
import com.jaly.touchscreenor.util.CommandExecution;
import com.jaly.touchscreenor.util.ParamSettingSteps;

public class TaskService extends Service {

	private final static String TAG = "TaskService";
	private CommandExecution ce;
	private TaskManager taskManager;
	private ScriptManager scriptManager;

	// 定时开始任务
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_TIME_TICK)) {
				String curTime = ParamSettingSteps.getCurrentTimeString();
				List<TaskItem> items = taskManager.getAllTaskItems();
				for (TaskItem item : items) {
					if (item.isRunning() && curTime.equals(item.getStartTime())) {
						taskManager.startTask(TaskService.this, item, ce);
					}
				}
			}
		}
	};

	@Override
	public void onCreate() {
		taskManager = TaskManager.getManager(this);
		scriptManager = new ScriptManager(this);
		try {
			ce = new CommandExecution();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_TIME_TICK);
		registerReceiver(receiver, filter);
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		boolean flag = true;
		if (intent.hasExtra("atOnce")) {
			String fileName = intent.getStringExtra("atOnce");
			try {
				TagScript tagScript = scriptManager.readBinary(fileName); 
				Set<String> paramNames = tagScript.getParamMap().keySet();
				TaskItem taskItem = new TaskItem();
				taskItem.setFileName(fileName);
				taskItem.setTagScript(tagScript);
				if (!paramNames.isEmpty()) {
					Map<String, String> paramMap = ParamSettingSteps
							.showParamsDialog(this, paramNames);
					if (paramMap != null) {
						tagScript.injectParamValue(paramMap);
					} else {
						flag = false;
					}
				}
				if (flag) {
					taskManager.startTask(this, taskItem, ce);
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		} else if (intent.hasExtra("onTime")) {
			String fileName = intent.getStringExtra("onTime"); 
			try {
				TagScript tagScript = scriptManager.readBinary(fileName);
				Set<String> paramNames = tagScript.getParamMap().keySet();
				TaskItem taskItem = new TaskItem();
				taskItem.setFileName(fileName);
				taskItem.setTagScript(tagScript);
				if (!paramNames.isEmpty()) {
					Map<String, String> paramMap = ParamSettingSteps
							.showParamsDialog(this, paramNames);
					if (paramMap != null) {
						tagScript.injectParamValue(paramMap);
						taskItem.setParamMap(paramMap);
					} else {
						flag = false;
					}
				}
				if (flag) {
					String time = ParamSettingSteps.showTimerDialog(this);
					if (time != null) {
						taskItem.setStartTime(time);
						taskManager.addTask(taskItem);
						Intent it = new Intent();
						it.setAction(MainActivity.ACTION_REFRESHTASK);
						sendBroadcast(it);
					}
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			} 
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		if (ce != null) {
			try {
				ce.destroy();
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		unregisterReceiver(receiver);
		super.onDestroy();
	}
}
