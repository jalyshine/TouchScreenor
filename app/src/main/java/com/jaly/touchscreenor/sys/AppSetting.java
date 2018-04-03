package com.jaly.touchscreenor.sys;


public class AppSetting {

	private int appListType = 0;        // 应用列表类型 【0所有应用、1系统应用、2、第三方应用】
	private int appDefDelay = 3000;     // 应用启动的默认延迟时间
	private int operDefDelay = 1000;    // 任意操作的默认延迟时间
	private int activityTimeout = 5000; // 页面切换的超时时间
	private int checkFreq = 1000;       // 页面超时检查的频率
	private boolean isUnicode = false;  // 是否使用Unicode

	public int getAppListType() {
		return appListType;
	}

	public void setAppListType(int appListType) {
		this.appListType = appListType;
	}

	public int getAppDefDelay() {
		return appDefDelay;
	}

	public void setAppDefDelay(int appDefDelay) {
		this.appDefDelay = appDefDelay;
	}

	public int getOperDefDelay() {
		return operDefDelay;
	}

	public void setOperDefDelay(int operDefDelay) {
		this.operDefDelay = operDefDelay;
	}

	public int getActivityTimeout() {
		return activityTimeout;
	}

	public void setActivityTimeout(int activityTimeout) {
		this.activityTimeout = activityTimeout;
	}

	public int getCheckFreq() {
		return checkFreq;
	}

	public void setCheckFreq(int checkFreq) {
		this.checkFreq = checkFreq;
	}

	public boolean isUnicode() {
		return isUnicode;
	}

	public void setUnicode(boolean isUnicode) {
		this.isUnicode = isUnicode;
	}

	@Override
	public String toString() {
		return "AppSetting [appListType=" + appListType + ", appDefDelay="
				+ appDefDelay + ", operDefDelay=" + operDefDelay
				+ ", activityTimeout=" + activityTimeout + ", checkFreq="
				+ checkFreq + ", isUnicode=" + isUnicode + "]";
	} 
	
}
