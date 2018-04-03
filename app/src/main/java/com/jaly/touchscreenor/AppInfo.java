package com.jaly.touchscreenor;

import java.util.List;

import android.graphics.drawable.Drawable;

/**
 * Model类 ，用来存储应用程序信息
 * @author Administrator
 *
 */
public class AppInfo{ 
	
	private String appLabel;     // 应用程序标签
	private String mainActivity; // 入口Activity
	private List<String> exportActivities;  // 可被其他应用启动的activity
	private Drawable appIcon;    // 应用程序图像 
	private String pkgName;      // 应用程序所对应的包名 
	private int vsnCode;         // 版本号

	public String getAppLabel() {
		return appLabel;
	}

	public void setAppLabel(String appName) {
		this.appLabel = appName;
	}

	public String getMainActivity() {
		return mainActivity;
	}

	public void setMainActivity(String mainActivity) {
		this.mainActivity = mainActivity;
	}

	public List<String> getExportActivities() {
		return exportActivities;
	}

	public void setExportActivities(List<String> exportActivities) {
		this.exportActivities = exportActivities;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	} 

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public int getVsnCode() {
		return vsnCode;
	}

	public void setVsnCode(int vsnCode) {
		this.vsnCode = vsnCode;
	}
	
}