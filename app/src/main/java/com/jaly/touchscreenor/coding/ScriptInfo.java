package com.jaly.touchscreenor.coding;

import java.io.Serializable;
import java.util.Date;

import android.graphics.drawable.Drawable;

/**
 * 封装脚本文件相关信息
 * 
 * @author Administrator
 * 
 */
public class ScriptInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String fileName;
	private String pkgName;
	private Date upateTime;
	private transient Drawable appIcon;
	
	public ScriptInfo(String fileName, String pkgName,
			Date upateTime) { 
		this.fileName = fileName; 
		this.pkgName = pkgName; 
		this.upateTime = upateTime;
	}

	public String getFileName() {
		return fileName;
	} 

	public String getPkgName() {
		return pkgName;
	} 

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

	public Date getUpateTime() {
		return upateTime;
	} 

}
