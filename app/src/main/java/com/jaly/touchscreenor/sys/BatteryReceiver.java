package com.jaly.touchscreenor.sys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

/**
 * 电量监测接收器
 * @author Administrator
 *
 */
public class BatteryReceiver extends BroadcastReceiver { 

	private TextView textView;
	
	public BatteryReceiver(TextView textView) {
		this.textView = textView;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		int current = intent.getExtras().getInt("level");
		int total = intent.getExtras().getInt("scale");
		int percent = current * 100 / total;
		textView.setText(percent + "%");
	}
}