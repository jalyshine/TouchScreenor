package com.jaly.touchscreenor.sys;

import android.content.Context;

public class AppSettingManager {
	
	private static AppSettingManager manager = new AppSettingManager(); 
	public AppSetting curSetting;
	private AppSettingDao dao;
	
	private AppSettingManager() {

	} 
	
	public AppSetting getCurSetting() {
		return curSetting;
	}
	
	public static AppSettingManager getInstance(){ 
		return manager;
	} 
	
	/**
	 * 提交应用设置
	 * @param appSetting
	 * @throws Exception
	 */
	public void submit(Context context, AppSetting setting){
		dao = new AppSettingDao(context);
		dao.set(setting);
	} 
	
	/**
	 * 读取设置
	 * @return
	 * @throws Exception
	 */
	public AppSetting read(Context context){ 
		dao = new AppSettingDao(context);
		curSetting = dao.get();
		if(curSetting == null){
			curSetting = new AppSetting();			
		}
		return curSetting;
	}
	
}
