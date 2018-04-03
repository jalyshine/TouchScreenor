package com.jaly.touchscreenor.floatwnd;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jaly.touchscreenor.AppInfo;
import com.jaly.touchscreenor.AppListAdapter;
import com.jaly.touchscreenor.R;
import com.jaly.touchscreenor.sys.AppSetting;
import com.jaly.touchscreenor.sys.AppSettingManager;
import com.jaly.touchscreenor.util.AppUtils;
import com.jaly.touchscreenor.util.ComfirmDialog;

public class FloatToolBar extends FloatWindow implements View.OnClickListener{ 
	
	private TextView txtTouch;
	private TextView txtNav;
	private TextView txtSubmit;
	private TextView txtReset;
	private TextView txtShell;
	private TextView txtApp;
	private TextView txtInput;
	private TextView txtRedo;
	private TextView txtUndo;
	private TextView txtExit;
	
	private boolean isShellOn;
	
	private FloatWndService service;
	private List<AppInfo> appInfos;
	private AppListAdapter appListAdapter;
	private int selItemIdx;
	
	public FloatToolBar(FloatWndService service) {
		super(service); 
		this.service = service;  
		initView();
		initEvent();
		initAppList();
	} 
	
	private void initView(){
		txtTouch = (TextView) findViewById(R.id.txt_tb_touch);
		txtNav = (TextView) findViewById(R.id.txt_tb_nav);
		txtSubmit = (TextView) findViewById(R.id.txt_tb_submit);
		txtReset = (TextView) findViewById(R.id.txt_tb_reset);
		txtShell = (TextView) findViewById(R.id.txt_tb_shell);

		txtApp = (TextView) findViewById(R.id.txt_tb_app);
		txtInput = (TextView) findViewById(R.id.txt_tb_input);
		txtRedo = (TextView) findViewById(R.id.txt_tb_redo);
		txtUndo = (TextView) findViewById(R.id.txt_tb_undo);
		txtExit = (TextView) findViewById(R.id.txt_tb_exit);
	}
	
	private void initEvent(){
		txtTouch.setOnClickListener(this);
		txtNav.setOnClickListener(this);
		txtSubmit.setOnClickListener(this);
		txtReset.setOnClickListener(this);
		txtShell.setOnClickListener(this);

		txtApp.setOnClickListener(this);
		txtInput.setOnClickListener(this);
		txtRedo.setOnClickListener(this);
		txtUndo.setOnClickListener(this);
		txtExit.setOnClickListener(this);
	}

	@Override
	public android.view.WindowManager.LayoutParams initParams(Context context) {
		LayoutInflater.from(context).inflate(R.layout.float_tool_bar, this);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.TOP;
		params.alpha = 0.9f;
		if(AppUtils.hasPermission(context, "android.permission.SYSTEM_ALERT_WINDOW")){
			params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;			
		} else {
			params.type = WindowManager.LayoutParams.TYPE_TOAST; 			
		}
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE 
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		return params;
	}
	
	private void updateY(int y){
		getParams().y = y;
		getManager().updateViewLayout(this, getParams());
	} 

	@Override
	public void doWork(final boolean isWorked) {
		final int step = 10;
		final int delay = 10; 
		new Thread(new Runnable() { 
			@Override
			public void run() {
				for(int i=1; i<=step; i++){  
					final float m = (float)(isWorked ? (i - step) : -i)/step;
					post(new Runnable() { 
						@Override
						public void run() {
							updateY((int)(m*getHeight()));
						}
					}); 
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						Log.e("FloatToolBar", e.getMessage());
					}
				} 
			}
		}).start();
	} 
	
	/**
	 * 创建应用程序列表， initCreateScript调用
	 * 
	 * @return
	 */
	private GridView getAppList() {
		GridView gridView = (GridView) View.inflate(service,
				R.layout.dialog_app_list, null);
		gridView.setAdapter(appListAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selItemIdx = position;
				appListAdapter.setSeclection(position);
				appListAdapter.notifyDataSetChanged();
			}
		});
		return gridView;
	}

	private RadioGroup getActivityList(List<String> activities) {
		RadioGroup radioGroup = new RadioGroup(service);
		int count = 0;
		for (String activity : activities) {
			RadioButton radio = new RadioButton(service);
			radio.setTextSize(12f);
			radio.setText(activity);
			if (count == 0) {
				radio.setChecked(true);
			}
			radio.setId(count++);
			radioGroup.addView(radio);
		}
		return radioGroup;
	}

	private void initAppList() {
		int filter = AppUtils.FILTER_ALL_APP;
		try {
			AppSetting setting = AppSettingManager.getInstance()
					.getCurSetting();
			switch (setting.getAppListType()) {
			case 0:
				filter = AppUtils.FILTER_ALL_APP;
				break;
			case 1:
				filter = AppUtils.FILTER_SYSTEM_APP;
				break;
			case 2:
				filter = AppUtils.FILTER_THIRD_APP;
				break;
			default:
				filter = AppUtils.FILTER_ALL_APP;
				break;
			}
		} catch (Exception e) {
			Log.e("FloatToolBar", e.getMessage());
		}
		final int FILTER = filter;
		new Thread(new Runnable() {
			@Override
			public void run() {
				appInfos = AppUtils.queryAppInfo(service, FILTER);
				appListAdapter = new AppListAdapter(service, appInfos);
			}
		}).start();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.txt_tb_app: 
				if (ComfirmDialog.showComfirmDialog(service, "请选择要控制的应用",
						getAppList())) {
					AppInfo info = appInfos.get(selItemIdx);
					String extra = info.getPkgName() + "|" + info.getVsnCode();
					ScrollView scrollView = new ScrollView(service);
					RadioGroup group = getActivityList(info.getExportActivities());
					scrollView.addView(group);
					if (ComfirmDialog.showComfirmDialog(service,
							"选择入口Activity", scrollView)) {
						int id = group.getCheckedRadioButtonId();
						extra += "|" + info.getExportActivities().get(id);
					}
					service.startApp(extra);					
				}
				break;
			case R.id.txt_tb_touch:
				service.callFloatWnd((byte)1);
				break;
			case R.id.txt_tb_nav:
				service.callFloatWnd((byte)2);
				break;
			case R.id.txt_tb_input:
				service.callFloatWnd((byte)3);
				break;
			case R.id.txt_tb_submit:
				service.submit();
				break;
			case R.id.txt_tb_reset: 
				service.getEncoding().reset();
				break;
			case R.id.txt_tb_shell:
				int rsc = isShellOn?R.drawable.tb_shell_on:R.drawable.tb_shell_off;
				setImage((TextView)v, rsc);
				// 设置是否同步执行shell命令
				service.setShellEnabled(isShellOn);
				isShellOn = !isShellOn;
				break;
			case R.id.txt_tb_undo: 
				String message = service.getEncoding().undo();
				AppUtils.serviceToast(service, "撤销操作：" + message); 
				break;
			case R.id.txt_tb_redo: 
				message = service.getEncoding().redo();
				AppUtils.serviceToast(service, "恢复操作：" + message);  
			case R.id.txt_tb_exit:
				service.exit();
				break;
			default:
				break;
		}
	}

	/**
	 * 设置TextView的图片
	 * @param textView
	 * @param resource
	 */
	public void setImage(TextView textView, int resource){ 
		Drawable top = getResources().getDrawable(resource); 
		textView.setCompoundDrawablesWithIntrinsicBounds(null,
				top, null, null);
	}
	
}
