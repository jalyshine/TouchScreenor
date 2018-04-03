package com.jaly.touchscreenor.sys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * �?��启动接收�?
 * @author Administrator
 *
 */
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) { 
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
			Intent startIntent = new Intent(context, TaskService.class); 
			context.startService(startIntent);
        }
	}

}
