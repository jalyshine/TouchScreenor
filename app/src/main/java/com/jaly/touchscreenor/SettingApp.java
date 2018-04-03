package com.jaly.touchscreenor;

import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.jaly.touchscreenor.sys.AppSetting;
import com.jaly.touchscreenor.sys.AppSettingManager;

@ContentView(R.layout.setting_app)
public class SettingApp extends Activity {
	
	private final static String TAG = SettingApp.class.getSimpleName();
	
	@ViewInject(R.id.btn_setting_back)
	private Button btnBack;
	@ViewInject(R.id.setting_app_style)
	private RadioGroup appStyle;
	@ViewInject(R.id.setting_app_all)
	private RadioButton appAll;
	@ViewInject(R.id.setting_app_sys)
	private RadioButton appSys;
	@ViewInject(R.id.setting_app_thd)
	private RadioButton appThd;
	
	private AppSettingManager manager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		x.view().inject(this);
		manager = AppSettingManager.getInstance(); 
		
		btnBack.setText(R.string.setting_app_style); 
		try {
			AppSetting setting = manager.getCurSetting(); 
			switch (setting.getAppListType()) {
				case 0: appAll.setChecked(true); break;
				case 1: appSys.setChecked(true); break;
				case 2: appThd.setChecked(true); break;
				default: break;
			} 
		} catch (Exception e) { 
			Log.e(TAG, e.getMessage());
		}
	} 

	@Event(value={R.id.setting_app_style}, type=OnCheckedChangeListener.class)
	private void onCheckedChanged(RadioGroup group, int checkedId) {
		AppSetting setting = manager.getCurSetting();
		int appListType = 0;
		switch (checkedId) {
		case R.id.setting_app_all:
			appListType = 0;
			break;
		case R.id.setting_app_sys:
			appListType = 1;
			break;
		case R.id.setting_app_thd:
			appListType = 2;
			break;
		default:
			break;
		}
		setting.setAppListType(appListType);
		manager.submit(this, setting);
	}

	@Event(value={R.id.btn_setting_back})
	private void onClick(View v) {
		if(v.getId() == R.id.btn_setting_back){
			finish();
		}
	}
	
}
