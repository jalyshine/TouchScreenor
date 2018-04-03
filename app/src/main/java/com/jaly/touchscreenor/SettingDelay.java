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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.jaly.touchscreenor.sys.AppSetting;
import com.jaly.touchscreenor.sys.AppSettingManager;

@ContentView(R.layout.setting_delay)
public class SettingDelay extends Activity implements OnSeekBarChangeListener{
	
	private final static String TAG = SettingDelay.class.getSimpleName();

	@ViewInject(R.id.btn_setting_back)
	private Button btnBack;
	@ViewInject(R.id.seek_start_delay)
	private SeekBar seekStart;
	@ViewInject(R.id.seek_oper_delay)
	private SeekBar seekOper;
	
	private AppSettingManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE); 
		x.view().inject(this);
		manager = AppSettingManager.getInstance();
		initViews();
		initSetting();
	}
	
	private void initViews(){ 
		btnBack.setText(R.string.setting_app_style); 
		seekStart.setMax(10000);
		seekOper.setMax(5000);
		seekStart.setOnSeekBarChangeListener(this);
		seekOper.setOnSeekBarChangeListener(this);
	}
	
	private void initSetting(){
		try {
			AppSetting setting = manager.getCurSetting();
			seekStart.setProgress(setting.getAppDefDelay());
			seekOper.setProgress(setting.getOperDefDelay());
		} catch (Exception e) { 
			Log.e(TAG, e.getMessage());
		}
	} 

	@Event(value={R.id.btn_setting_back})
	private void onClick(View v) {
		if(v.getId() == R.id.btn_setting_back){
			finish();
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		int progress_x = (progress * 10 / seekBar.getMax()) * (seekBar.getMax() / 10); 
		seekBar.setProgress(progress_x); 
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { 
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		AppSetting setting = manager.getCurSetting();
		int value = seekBar.getProgress();
		String obj = null;
		if(seekBar == seekStart){ 
			setting.setAppDefDelay(value);
			obj = getResources().getString(R.string.setting_start_delay);
		} else if(seekBar == seekOper){
			setting.setOperDefDelay(value);
			obj = getResources().getString(R.string.setting_oper_delay);
		}
		manager.submit(this, setting);
		Toast.makeText(this, obj + ":" + value, Toast.LENGTH_SHORT).show();
	}
}
