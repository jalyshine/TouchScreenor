package com.jaly.touchscreenor.floatwnd;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

import com.jaly.touchscreenor.EditScriptActivity;
import com.jaly.touchscreenor.MainActivity;
import com.jaly.touchscreenor.coding.ScriptEncoding;
import com.jaly.touchscreenor.exception.VsnMismatchingException;
import com.jaly.touchscreenor.util.AppUtils;
import com.jaly.touchscreenor.util.CommandExecution;

public class FloatWndService extends Service {

	private static final int HANDLE_CHECK_ACTIVITY = 200;
	private static final String TAG = "FloatWndService";

	private WindowManager manager;
	private Handler mHandler;

	private FloatToolBar toolBar;
	private FloatGhost ghost;
	private FloatTouchPad touchPad;
	private FloatNavPad navPad;
	private FloatInputBox inputBox;
	private FloatDescBox descBox;

	private ScriptEncoding encoding;
	private CommandExecution ce;
	private boolean isShellEnabled = true;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressLint("HandlerLeak")
	@Override
	public void onCreate() { 
		super.onCreate();
		manager = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == HANDLE_CHECK_ACTIVITY) {
					sendEmptyMessageDelayed(HANDLE_CHECK_ACTIVITY, 1000);
				}
			}
		};
	}

	/**
	 * 切换操作板，在toolBar中调用
	 * 
	 * @param flag
	 *            true:touchPad false:navPad
	 */
	public void callFloatWnd(byte type) {
		switch (type) {
		case 0:
			toolBar.setWorked(!toolBar.isWorked());
			break;
		case 1:
			boolean flag = touchPad.isWorked();
			if (!flag) {
				toolBar.setWorked(false);
				navPad.setWorked(false);
				inputBox.setWorked(false);
			}
			touchPad.setWorked(!flag);
			break;
		case 2:
			flag = navPad.isWorked();
			if (!flag) {
				toolBar.setWorked(false);
				touchPad.setWorked(false);
				inputBox.setWorked(false);
			}
			navPad.setWorked(!flag);
			break;
		case 3:
			flag = inputBox.isWorked();
			if (!flag) {
				toolBar.setWorked(false);
				touchPad.setWorked(false);
				navPad.setWorked(false);
			}
			inputBox.setWorked(!flag);
			break;
		case 4:
			descBox.setWorked(true);
			break;
		default:
			break;
		}
		if(toolBar.isWorked()){
			descBox.setWorked(false);
		}
	}

	/**
	 * 获取窗体管理器， 在FloatWindow中调用
	 * 
	 * @return
	 */
	public WindowManager getManager() {
		return manager;
	}

	/**
	 * 获取编码器
	 * 
	 * @return
	 */
	public ScriptEncoding getEncoding() {
		return encoding;
	}

	/**
	 * 判断是否同步执行shell命令
	 * 
	 * @return
	 */
	public boolean isShellEnabled() {
		return isShellEnabled;
	}

	/**
	 * 设置同步执行shell命令
	 * 
	 * @param isShellEnabled
	 */
	public void setShellEnabled(boolean isShellEnabled) {
		this.isShellEnabled = isShellEnabled;
	}
	
	public void startApp(String extra){
		String[] tokens = extra.split("\\|"); 
		try {
			String cls = null;
			if (tokens.length == 3) {
				cls = tokens[2];
			}
			int vsnCode = Integer.parseInt(tokens[1]);
			String pkg = tokens[0];
			// 初始化编码器
			encoding = new ScriptEncoding(pkg, cls, vsnCode, ce);
			// 启动应用
			AppUtils.startActivity(getApplicationContext(), pkg, cls);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			stopSelf();
		} catch (VsnMismatchingException e) { 
			Log.e(TAG, e.getMessage()); 
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) { 
		if(intent.hasExtra("app")){
			try { 
				//初始化编码器
				ce = new CommandExecution(); 
				
				ghost = new FloatGhost(this);
				toolBar = new FloatToolBar(this); 
				touchPad = new FloatTouchPad(this);
				navPad = new FloatNavPad(this);
				inputBox = new FloatInputBox(this);
				descBox = new FloatDescBox(this);
				
				touchPad.setWorked(false);
				navPad.setWorked(false);
				inputBox.setWorked(false);				
				descBox.setWorked(false);				
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				stopSelf();
			} 
		} 
		mHandler.removeMessages(HANDLE_CHECK_ACTIVITY);
		mHandler.sendEmptyMessage(HANDLE_CHECK_ACTIVITY);
		return Service.START_NOT_STICKY;
	}

	/**
	 * 保存脚本到临时文件
	 */
	public void submit() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// 创建临时文件，并将代码写入其中
					String content = encoding.write();
					// 启动代码编辑器
					Intent intent = new Intent(getApplicationContext(),
							EditScriptActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("content", content);
					getApplicationContext().startActivity(intent);
					// 停止该服务
					stopSelf();
				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}).start();
	}

	/**
	 * 退出编码
	 */
	public void exit() {
		// 启动主界面
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplicationContext().startActivity(intent);
		// 停止该服务
		stopSelf();
	}

	/**
	 * 释放所有资源
	 */
	@Override
	public void onDestroy() { 
		try {
			ce.destroy();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		// 卸载所有悬浮窗
		descBox.install(false);
		inputBox.install(false);
		navPad.install(false);
		touchPad.install(false);
		toolBar.install(false);
		ghost.install(false);
		super.onDestroy();
	} 

}
