package com.jaly.touchscreenor.floatwnd;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.jaly.touchscreenor.R;
import com.jaly.touchscreenor.coding.TagSelect.Direct;
import com.jaly.touchscreenor.util.AppUtils;

public class FloatNavPad extends FloatWindow implements View.OnClickListener {

	private ImageButton okBtn;
	private ImageButton backBtn;
	private ImageButton leftBtn;
	private ImageButton rightBtn;
	private ImageButton upBtn;
	private ImageButton downBtn;

	private Context context;

	private FloatWndService service;

	public FloatNavPad(FloatWndService service) {
		super(service);
		this.service = service;

		okBtn = (ImageButton) findViewById(R.id.img_direct_ok);
		backBtn = (ImageButton) findViewById(R.id.img_direct_back);
		leftBtn = (ImageButton) findViewById(R.id.img_direct_left);
		rightBtn = (ImageButton) findViewById(R.id.img_direct_right);
		upBtn = (ImageButton) findViewById(R.id.img_direct_up);
		downBtn = (ImageButton) findViewById(R.id.img_direct_down);

		okBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		leftBtn.setOnClickListener(this);
		rightBtn.setOnClickListener(this);
		upBtn.setOnClickListener(this);
		downBtn.setOnClickListener(this);
	}

	@Override
	public android.view.WindowManager.LayoutParams initParams(Context context) {
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.float_nav_pad, this);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.alpha = 0.9f;
		params.format = PixelFormat.RGBA_8888;
		if (AppUtils.hasPermission(context,
				"android.permission.SYSTEM_ALERT_WINDOW")) {
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
		setVisibility(isWorked ? VISIBLE : INVISIBLE);
	}

	@Override
	public void onClick(View v) {
		Direct direct = null;
		switch (v.getId()) {
		case R.id.img_direct_ok:
			direct = Direct.ok;
			break;
		case R.id.img_direct_back:
			direct = Direct.back;
			break;
		case R.id.img_direct_left:
			direct = Direct.left;
			break;
		case R.id.img_direct_right:
			direct = Direct.right;
			break;
		case R.id.img_direct_up:
			direct = Direct.up;
			break;
		case R.id.img_direct_down:
			direct = Direct.down;
			break;
		}
		final Direct finalDirect = direct;
		new Thread(new Runnable() {
			@Override
			public void run() {
				String activity = AppUtils.getRunningActivityName(context); 
				try {
					service.getEncoding().addSelect(activity, finalDirect,
								service.isShellEnabled());
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
