package com.jaly.touchscreenor.floatwnd;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.jaly.touchscreenor.R;
import com.jaly.touchscreenor.floatwnd.FloatWindow.OnOperListener;
import com.jaly.touchscreenor.util.AppUtils;

public class FloatTouchPad extends FloatWindow 
		implements View.OnClickListener, OnOperListener{

	private ImageView imgSwitch;
	private boolean isSwipeOn = false;
	private Context context; 
	private FloatWndService service;
	
	public FloatTouchPad(FloatWndService service) {
		super(service); 
		this.service = service; 
		canBeMoved(this, FLAG_MOVE_ALL);
		
		imgSwitch = (ImageView) findViewById(R.id.img_touch_switch);
		imgSwitch.setOnClickListener(this);
	}
	
	@Override
	public android.view.WindowManager.LayoutParams initParams(Context context) {
		
		this.context = context;
		
		LayoutInflater.from(context).inflate(R.layout.float_touch_pad, this);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.alpha = 0.9f;
		params.format = PixelFormat.RGBA_8888;
		if(AppUtils.hasPermission(context, "android.permission.SYSTEM_ALERT_WINDOW")){
			params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;			
		} else {
			params.type = WindowManager.LayoutParams.TYPE_TOAST; 			
		}
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; 
		return params;
	}

	@Override
	public void doWork(boolean isWorked) {
		setVisibility(isWorked?VISIBLE : INVISIBLE);
	} 

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.img_touch_switch){
			if(isSwipeOn){
				imgSwitch.setImageResource(R.drawable.swipe_off);
			} else {
				imgSwitch.setImageResource(R.drawable.swipe_on);				
			}
			isSwipeOn = !isSwipeOn;
		}  
	}

	@Override
	public void onFloatWindowClick(View view, final Point pos) { 
		setVisibility(INVISIBLE);
		new Thread(new Runnable() { 
			@Override
			public void run() {
				String activity = AppUtils.getRunningActivityName(context); 
				try {
					service.getEncoding().addClick(activity, pos, service.isShellEnabled());
				} catch (Exception e) { 
					e.printStackTrace();
				} 
				postDelayed(new Runnable() { 
					@Override
					public void run() {
						setVisibility(View.VISIBLE);
						service.callFloatWnd((byte)4);
					}
				}, 100); 
			}
		}).start();
	}

	@Override
	public void onFloatWindowSwipe(View view, final Point start, final Point end) {
		if(isSwipeOn){
			new Thread(new Runnable() { 
				@Override
				public void run() { 
					String activity = AppUtils.getRunningActivityName(context);
					try {
						service.getEncoding().addSwipe(activity, start, end, service.isShellEnabled());
					} catch (Exception e) { 
						e.printStackTrace();
					} 
					post(new Runnable() {
						public void run() {
							service.callFloatWnd((byte)4);
						}
					});
				}
			}).start();
		}
	}

}
