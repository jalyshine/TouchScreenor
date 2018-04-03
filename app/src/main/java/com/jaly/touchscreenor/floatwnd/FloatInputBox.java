package com.jaly.touchscreenor.floatwnd;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.jaly.touchscreenor.R;
import com.jaly.touchscreenor.util.AppUtils;
import com.jaly.touchscreenor.util.InputUtils;

public class FloatInputBox extends FloatWindow implements OnClickListener {

	private EditText inputBox;
	private Button okBtn;
	private Button cancelBtn;
	private Context context;
	private boolean isInputFocus;

	private FloatWndService service;

	public FloatInputBox(FloatWndService service) {
		super(service);
		this.service = service;
		inputBox = (EditText) findViewById(R.id.edit_input_box);
		okBtn = (Button) findViewById(R.id.btn_input_ok);
		cancelBtn = (Button) findViewById(R.id.btn_input_cancel);

		okBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		inputBox.setOnClickListener(this);
	}

	@Override
	public android.view.WindowManager.LayoutParams initParams(Context context) {
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.float_input_box, this);
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

	public void checkInputFocus(boolean flag) {
		if (flag) {
			getParams().flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
			getManager().updateViewLayout(this, getParams());
			InputUtils.Keyboard(inputBox, true);
		} else {
			inputBox.setText("");
			InputUtils.ShowKeyboard(inputBox, false);
			getParams().flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
					| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
			getManager().updateViewLayout(this, getParams());
		}
		isInputFocus = flag;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_input_ok:
			final String content = inputBox.getText().toString();
			checkInputFocus(false);
			new Thread(new Runnable() {
				@Override
				public void run() {
					String activity = AppUtils.getRunningActivityName(context);
					try {
						service.getEncoding().addInput(activity, content,
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
			break;
		case R.id.btn_input_cancel:
			checkInputFocus(false);
			break;
		case R.id.edit_input_box:
			if (!isInputFocus) {
				checkInputFocus(true);
			}
			break;
		default:
			break;
		}
	}

}
