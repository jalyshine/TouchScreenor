package com.jaly.touchscreenor;

import org.xutils.x;

import android.app.Application;

public class TouchScreenorApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
		x.Ext.init(this); 
		x.Ext.setDebug(false);
	}
	
}
