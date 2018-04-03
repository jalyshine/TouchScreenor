package com.jaly.touchscreenor.sys;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;

import com.jaly.touchscreenor.MainActivity;
import com.jaly.touchscreenor.coding.TagAction;
import com.jaly.touchscreenor.coding.TagScript;
import com.jaly.touchscreenor.exception.VsnMismatchingException;
import com.jaly.touchscreenor.util.AppUtils;
import com.jaly.touchscreenor.util.CommandExecution;
import com.jaly.touchscreenor.util.InputUtils;
import com.jaly.touchscreenor.util.ScreenUtils;

public class TaskManager {

	private static TaskManager manager = new TaskManager();  

	private TaskItemDao dao;
	private int freq;    // 检查频率
	private int timeout; // 超时上限
	private boolean isUnicode;  // 是否使用Unicode

	private TaskManager(){
		// 初始化，读取系统设置
		AppSetting setting = AppSettingManager.getInstance().getCurSetting();
		freq = setting.getCheckFreq();
		timeout = setting.getActivityTimeout();
		isUnicode = setting.isUnicode();
	} 
	
	public static TaskManager getManager(Context context) {
		manager.dao = new TaskItemDao(context);
		return manager;
	} 

	public void addTask(TaskItem taskItem) { 
		dao.add(taskItem);
	}

	public void removeTask(Integer id) { 
		dao.delete(id);
	}
	
	public void updateTask(TaskItem taskItem){
		dao.update(taskItem);
	}

	public TaskItem getTaskItem(Integer id) { 
		return dao.get(id);
	}

	public List<TaskItem> getAllTaskItems() { 
		return dao.getAll();
	}
	
	/**
	 * 直接开始任务
	 * @param taskItem
	 */
	public void startTask(final Context context, 
			TaskItem taskItem, final CommandExecution ce){
		final TagScript tagScript = taskItem.getTagScript();
		final boolean isOnTime = (taskItem.getStartTime() != null);
		final String fileName = taskItem.getFileName();				
		AppUtils.serviceToast(context, fileName + " 任务开始!");
		new Thread(new Runnable() {
			@Override
			public void run() {
				doWork(tagScript, ce, context, isOnTime); 
				// 返回到主界面
				Intent intent = new Intent(context, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				// 显示消息
				AppUtils.serviceToast(context, fileName + " 任务完成！");
			}
		}).start();
	}

	/**
	 * 启动目标应用，并执行脚本
	 * 
	 * @param tagScript
	 * @param ce
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws NameNotFoundException
	 * @throws VsnMismatchingException
	 */
	public void doWork(TagScript tagScript, CommandExecution ce,
			Context context, boolean isOnTime) {
		try {
			if (isOnTime) {
				// 点亮并解锁屏幕
				if (ScreenUtils.isScreenLock(context)) {
					ScreenUtils.wakeUpAndUnlock(context, ce);
				}
			}
			// 启动目标应用
			AppUtils.startActivity(context, tagScript.getPkgName(),
					tagScript.getMainActivity(), tagScript.getVsnCode());
			// 获取当前输入法
			String curIme = InputUtils.getCurIME(context);
			if(isUnicode){
				// 切换内嵌输入法
				InputUtils.switchIME(context, null);			
			}
			// 应用启动延迟
			Thread.sleep(tagScript.getDelay());
			// 执行脚本代码
			execScriptCode(tagScript.getActions(), ce, context);
			if(isUnicode){
				// 切换系统输入法
				InputUtils.switchIME(context, curIme);			
			}
			// 返回到主界面
			Intent intent = new Intent(context, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			 
		} catch (VsnMismatchingException e) { 
			
		} 
	}

	/**
	 * 具体执行脚本动作
	 * 
	 * @param actions
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void execScriptCode(List<TagAction> actions, CommandExecution ce,
			Context context) throws InterruptedException, IOException {
		int tolTime = 0; // 等待总时间
		for (TagAction action : actions) {
			String curActivity = action.getActivity(); 			
			String runningActivity = AppUtils.getRunningActivityName(context);
			List<String> shellCmds = action.getShellCmds();
			for (String cmd : shellCmds) {
				while (!runningActivity.equals(curActivity)) {
					Thread.sleep(freq);
					tolTime += freq;
					if (tolTime >= timeout) {
						return;
					}
					runningActivity = AppUtils.getRunningActivityName(context);
				}
				ce.execScript(cmd);
			} // for end
		} // for end
	}
	
}
