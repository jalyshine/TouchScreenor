package com.jaly.touchscreenor.floatwnd;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.jaly.touchscreenor.R;
import com.jaly.touchscreenor.util.AppUtils;
import com.jaly.touchscreenor.util.InputUtils;

public class FloatDescBox extends FloatWindow implements OnClickListener {

	private EditText descBox;
	private ImageView okBtn;
	private boolean isInputFocus;

	private FloatWndService service;

	public FloatDescBox(FloatWndService service) {
		super(service);
		this.service = service;
		descBox = (EditText) findViewById(R.id.edit_dialog_desc);
		okBtn = (ImageView) findViewById(R.id.img_desc_ok);

		okBtn.setOnClickListener(this); 
		descBox.setOnClickListener(this);
	}

	@Override
	public android.view.WindowManager.LayoutParams initParams(Context context) { 
		LayoutInflater.from(context).inflate(R.layout.float_desc_box, this);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.alpha = 0.9f;
		params.gravity = Gravity.TOP;
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

	public void checkInputFocus(boolean flag) {
		if (flag) {
			getParams().flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
			getManager().updateViewLayout(this, getParams());
			InputUtils.Keyboard(descBox, true);
		} else {
			descBox.setText("");
			InputUtils.ShowKeyboard(descBox, false);
			getParams().flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
					| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
			getManager().updateViewLayout(this, getParams());
		}
		isInputFocus = flag;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_desc_ok:
			final String content = descBox.getText().toString();
			checkInputFocus(false);
			new Thread(new Runnable() {
				@Override
				public void run() {
					service.getEncoding().setDescription(content);
					post(new Runnable() { 
						@Override
						public void run() {
							setWorked(false);
						}
					});
				}
			}).start();
			break; 
		case R.id.edit_dialog_desc:
			if (!isInputFocus) {
				checkInputFocus(true);
			}
			break;
		default:
			break;
		}
	}

}
