package com.jaly.touchscreenor;

import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.jaly.touchscreenor.sys.AppSetting;
import com.jaly.touchscreenor.sys.AppSettingManager;

@ContentView(R.layout.fragment_main_setting)
public class FragmentSetting extends Fragment {

	private final static String TAG = FragmentSetting.class.getSimpleName();

	@ViewInject(R.id.btn_app_style)
	private Button btnApp;
	@ViewInject(R.id.btn_script_delay)
	private Button btnDelay;
	@ViewInject(R.id.btn_page_skip)
	private Button btnVerify;
	@ViewInject(R.id.chk_use_unicode)
	private CheckBox isUnicode;

	private AppSettingManager manager;
	private Activity mainActivity;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return x.view().inject(this, inflater, container);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mainActivity = getActivity();
		manager = AppSettingManager.getInstance();
		try {
			AppSetting setting = manager.getCurSetting();
			isUnicode.setChecked(setting.isUnicode());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Event(value = { R.id.btn_app_style, R.id.btn_script_delay,
			R.id.btn_page_skip }, type = OnClickListener.class)
	private void onClick(View v) {
		Class<?> clazz = null;
		switch (v.getId()) {
		case R.id.btn_app_style:
			clazz = SettingApp.class;
			break;
		case R.id.btn_script_delay:
			clazz = SettingDelay.class;
			break;
		case R.id.btn_page_skip:
			clazz = SettingVerify.class;
			break;
		}
		if (clazz != null) {
			Intent intent = new Intent(mainActivity, clazz);
			mainActivity.startActivity(intent);
		}
	}

	@Event(value = { R.id.chk_use_unicode }, type = OnCheckedChangeListener.class)
	private void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		AppSetting setting = manager.getCurSetting();
		setting.setUnicode(isChecked);
		manager.submit(mainActivity, setting);
	}
}
