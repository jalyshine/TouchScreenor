package com.jaly.touchscreenor.floatwnd;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.jaly.touchscreenor.R;
import com.jaly.touchscreenor.floatwnd.FloatWindow.OnOperListener;
import com.jaly.touchscreenor.util.AppUtils;

public class FloatGhost extends FloatWindow implements OnOperListener{
	
	private FloatWndService service;
	
	public FloatGhost(FloatWndService service) {
		super(service);
		this.service = service;
		canBeMoved(this, FLAG_MOVE_Y);
	}

	@Override
	public android.view.WindowManager.LayoutParams initParams(Context context) { 
		LayoutInflater.from(context).inflate(R.layout.float_ghost, this);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.END;
		params.alpha = 0.8f; 
		params.format = PixelFormat.RGBA_8888;
		if(AppUtils.hasPermission(context, "android.permission.SYSTEM_ALERT_WINDOW")){
			params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;			
		} else {
			params.type = WindowManager.LayoutParams.TYPE_TOAST; 			
		} 			
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE ;  
		return params;
	}

	@Override
	public void doWork(boolean isWorked) {
		setVisibility(isWorked?VISIBLE : INVISIBLE);
	}

	@Override
	public void onFloatWindowClick(View view, Point pos) {
		service.callFloatWnd((byte)0);
	}

	@Override
	public void onFloatWindowSwipe(View view, Point start, Point end) {

	} 

}
