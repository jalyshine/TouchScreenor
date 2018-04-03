package com.jaly.touchscreenor;

import java.util.ArrayList;
import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.jaly.touchscreenor.sys.AppSettingManager;
import com.jaly.touchscreenor.sys.TaskService;

@ContentView(R.layout.activity_main)
public class MainActivity extends FragmentActivity {

	@ViewInject(R.id.id_viewpager)
	private ViewPager mViewPager;

	@ViewInject(R.id.id_title_bar_txt)
	private TextView mTitleBar;
	
	@ViewInject(R.id.btn_main_task)
	private Button taskBtn;
	
	@ViewInject(R.id.btn_main_script)
	private Button scriptBtn;
	
	@ViewInject(R.id.btn_main_setting)
	private Button settingBtn;
	
	@ViewInject(R.id.btn_main_help)
	private Button helpBtn;

	private FragmentPagerAdapter mAdapter;
	private List<Fragment> mFragments;
	private FragmentScript fragmentScript;
	private FragmentTask fragmentTask;
	private FragmentSetting fragmentSetting;
	private FragmentHelp fragmentHelp;
	
	public static final String ACTION_REFRESHTASK = "refresh_task";
	public static final String ACTION_REFRESHSCRIPT = "refresh_script";
	
	private BroadcastReceiver receiver = new BroadcastReceiver(){ 
		@Override
		public void onReceive(Context context, Intent intent) {
			if(ACTION_REFRESHTASK.equals(intent.getAction())){
				fragmentTask.refreshTaskList();
			} else if(ACTION_REFRESHSCRIPT.equals(intent.getAction())){
				fragmentScript.refreshScriptList();
			}
		} 
	};
	
	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
		@Override
		// 页面滚动事件
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) { }
		@Override
		// 页面选中事件
		public void onPageSelected(int position) {
			resetBottom();
			selectTab(position);
		}
		@Override
		// 页面滚动状态改变事件
		public void onPageScrollStateChanged(int state) { }
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);		
		x.view().inject(this);
		initDatas();
		selectTab(0);
		// 读取配置文件  
		AppSettingManager.getInstance().read(this); 
		// 注册广播接收器
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_REFRESHTASK);
		filter.addAction(ACTION_REFRESHSCRIPT);
		registerReceiver(receiver, filter);
	}

	private void initDatas() {
		mFragments = new ArrayList<Fragment>();
		fragmentScript = new FragmentScript();
		fragmentTask = new FragmentTask();
		fragmentHelp = new FragmentHelp();
		fragmentSetting = new FragmentSetting();
		// 将四个Fragment加入集合中
		mFragments.add(fragmentScript);
		mFragments.add(fragmentTask);
		mFragments.add(fragmentSetting);
		mFragments.add(fragmentHelp);
		// 初始化适配器
		mAdapter = new MainFragmentPageAdapter(getSupportFragmentManager(), mFragments);
		mViewPager.setAdapter(mAdapter);
		// 设置ViewPager的切换监听
		mViewPager.setOnPageChangeListener(onPageChangeListener);
	} 

	@Event(value={R.id.btn_main_script, R.id.btn_main_task, 
			R.id.btn_main_setting, R.id.btn_main_help})
	private void onClick(View v) {
		resetBottom();
		switch (v.getId()) {
		case R.id.btn_main_script:
			selectTab(0);
			break;
		case R.id.btn_main_task:
			selectTab(1);
			break;
		case R.id.btn_main_setting:
			selectTab(2);
			break;
		case R.id.btn_main_help:
			selectTab(3);
			break;
		}
	}

	private void selectTab(int i) {
		switch (i) {
		case 0:
			setBtnIcon(scriptBtn, R.drawable.fg_script_show, true);
			mTitleBar.setText(R.string.btn_main_script);
			break;
		case 1:
			setBtnIcon(taskBtn, R.drawable.fg_task_show, true);
			mTitleBar.setText(R.string.btn_main_task);
			break;
		case 2:
			setBtnIcon(settingBtn, R.drawable.fg_setting_show, true);
			mTitleBar.setText(R.string.btn_main_setting);
			break;
		case 3:
			setBtnIcon(helpBtn, R.drawable.fg_help_show, true);
			mTitleBar.setText(R.string.btn_main_help);
			break;
		}
		mViewPager.setCurrentItem(i);
	}

	private void resetBottom() {
		setBtnIcon(scriptBtn, R.drawable.fg_script_hidden, false);
		setBtnIcon(taskBtn, R.drawable.fg_task_hidden, false);
		setBtnIcon(settingBtn, R.drawable.fg_setting_hidden, false);
		setBtnIcon(helpBtn, R.drawable.fg_help_hidden, false);
	}

	private void setBtnIcon(Button button, int rid, boolean isLight) {
		Drawable top = getResources().getDrawable(rid);
		button.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
		button.setTextColor(isLight ? 0xff22aa22 : 0xff444444);
	} 
	
	/**
	 * 程序退出后，停止TaskService
	 */
	@Override
	protected void onDestroy() {
		stopService(new Intent(this, TaskService.class));
		super.onDestroy();
	}
	
}
