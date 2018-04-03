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

@ContentView(R.layout.setting_verify)
public class SettingVerify extends Activity implements OnSeekBarChangeListener{
	
	private final static String TAG = SettingVerify.class.getSimpleName();

	@ViewInject(R.id.btn_setting_back)
	private Button btnBack;
	@ViewInject(R.id.seek_skip_timeout)
	private SeekBar seekTimeout;
	@ViewInject(R.id.seek_check_freq)
	private SeekBar seekCheckfreq;
	
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
		seekTimeout.setMax(10000);
		seekCheckfreq.setMax(1000);
		seekTimeout.setOnSeekBarChangeListener(this);
		seekCheckfreq.setOnSeekBarChangeListener(this);
	}
	
	private void initSetting(){
		try {
			AppSetting setting = manager.getCurSetting();
			seekTimeout.setProgress(setting.getActivityTimeout());
			seekCheckfreq.setProgress(setting.getCheckFreq());
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
		if(seekBar == seekTimeout){ 
			setting.setActivityTimeout(value);
			obj = getResources().getString(R.string.setting_skip_timeout);
		} else if(seekBar == seekCheckfreq){
			setting.setCheckFreq(value);
			obj = getResources().getString(R.string.setting_check_freq);
		}
		manager.submit(this, setting);
		Toast.makeText(this, obj + ":" + value, Toast.LENGTH_SHORT).show();
	}
}
