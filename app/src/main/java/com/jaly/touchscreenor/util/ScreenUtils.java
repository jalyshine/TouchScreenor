package com.jaly.touchscreenor.util;

import java.io.IOException;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.os.PowerManager;
import android.view.KeyEvent;

/**
 * 屏幕控制相关工具
 * @author Administrator
 *
 */
@SuppressWarnings("deprecation")
public class ScreenUtils {

	/**
	 * 唤醒手机屏幕并解锁
	 * @throws IOException 
	 * @throws InterruptedException 
	 */ 
	public static void wakeUpAndUnlock(Context context, CommandExecution ce) 
			throws IOException, InterruptedException { 
	    // 屏幕解锁
	    KeyguardManager km = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE); 
	    KeyguardLock kl = km.newKeyguardLock("unLock");	    
		kl.reenableKeyguard(); // 屏幕锁定
		kl.disableKeyguard();  // 解锁 
		
	    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE); 
	    if (!pm.isScreenOn()) { 
			ce.execShellCmd("input keyevent " + KeyEvent.KEYCODE_POWER);
	    }
	}
	
	/**
	 * 判断屏幕亮
	 * @param context
	 * @return
	 */
	public static boolean isScreenOn(Context context) {
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE); 
		return pm.isScreenOn();
	}

	/**
	 * 判断是否锁屏
	 * @param context
	 * @return
	 */
	public static boolean isScreenLock(Context context) {
		KeyguardManager mKeyguardManager = (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE);
		return mKeyguardManager.inKeyguardRestrictedInputMode();
	}
	
}
