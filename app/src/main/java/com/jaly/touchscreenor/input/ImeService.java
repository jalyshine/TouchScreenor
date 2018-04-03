package com.jaly.touchscreenor.input;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.view.inputmethod.InputConnection;

import com.jaly.touchscreenor.R;
import com.jaly.touchscreenor.util.InputUtils;

public class ImeService extends InputMethodService {
	
	public final static String ACTION_APPEND = "com.jaly.ime.append";
	public final static String ACTION_REPLACE = "com.jaly.ime.replace";
	public final static String EXTRA_CONTENT = "content";

	private BroadcastReceiver receiver = new BroadcastReceiver() {		
		@Override
		public void onReceive(Context context, Intent intent) {
			InputConnection inputConnection = getCurrentInputConnection();
			String action = intent.getAction();
			if (ACTION_APPEND.equals(action) || ACTION_REPLACE.equals(action)) {
				if (ACTION_REPLACE.equals(action)) {
					inputConnection.deleteSurroundingText(1024, 1024);
				}
				if (intent.hasExtra(EXTRA_CONTENT)) {
					String content = intent.getStringExtra(EXTRA_CONTENT);
					content = InputUtils.fromUnicode(content);
					inputConnection.commitText(content, 1);
				} 
			}
		}
	};

	@Override
	public void onCreate() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_APPEND);
		filter.addAction(ACTION_REPLACE);
		registerReceiver(receiver, filter);
		super.onCreate();
	}

	@Override
	@SuppressLint("InflateParams")
	public View onCreateInputView() {
		View view = getLayoutInflater().inflate(R.layout.input_method_keyboard,
				null);
		return view;
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}
}
