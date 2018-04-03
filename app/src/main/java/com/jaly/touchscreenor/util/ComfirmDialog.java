package com.jaly.touchscreenor.util;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;

/**
 * 阻塞式模态对话框
 * @author Administrator
 *
 */
public class ComfirmDialog {

	int dialogResult;
	public static Handler handler;
	public Context context;
	
	public static boolean showComfirmDialog(Context context, String title, View view){
		handler = new MyHandler();
		return new ComfirmDialog(context, title, null, view).getResult() == 1;
	}

	public static boolean showComfirmDialog(Context context, String title, String message){
		handler = new MyHandler();
		return new ComfirmDialog(context, title, message, null).getResult() == 1;
	}
	
	private ComfirmDialog(Context context, String title, String message, View view) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setPositiveButton("确定", new DialogButtonOnClick(1));
		dialogBuilder.setNegativeButton("取消", new DialogButtonOnClick(2)); 
		if(title != null){
			dialogBuilder.setTitle(title);
		}
		if(message != null){
			dialogBuilder.setMessage(message);
		}
		if(view != null){
			dialogBuilder.setView(view);
		}
		AlertDialog dialog = dialogBuilder.create();
		if(context instanceof Service){
			dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		}
		dialog.show();	
		try {
			Looper.loop();
		} catch (Exception e) {
		}
	}
	
	public int getResult(){
		return dialogResult;
	}
	
	private static class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			throw new RuntimeException();
		}
	}
	
	private final class DialogButtonOnClick implements OnClickListener{
		int type;
		public DialogButtonOnClick(int type) {
			this.type = type;
		}
		@Override
		public void onClick(DialogInterface dialog, int which) {
			ComfirmDialog.this.dialogResult = type;
			Message m = handler.obtainMessage();
			handler.sendMessage(m);
		}
	}
}
