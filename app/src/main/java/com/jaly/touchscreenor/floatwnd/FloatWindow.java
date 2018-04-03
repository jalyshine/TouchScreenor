package com.jaly.touchscreenor.floatwnd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.LinearLayout;

public abstract class FloatWindow extends LinearLayout 
	implements View.OnTouchListener{

	private WindowManager manager;
	private WindowManager.LayoutParams params;
	private int height; // 像素
	private int width;  // 像素
	private boolean isInstalled;
	private boolean isWorked = true;
	
	private int lastX, lastY;
	private int paramX, paramY;
	private boolean isClick;
	
	public final static int FLAG_MOVE_ALL = 0;
	public final static int FLAG_MOVE_X = 1;
	public final static int FLAG_MOVE_Y = 2;
	private int move_flag = 0;
	
	interface OnOperListener{
		public abstract void onFloatWindowClick(View view, Point pos);
		public abstract void onFloatWindowSwipe(View view, Point start, Point end);
	}
	
	private OnOperListener onOperateListener = null;

	public FloatWindow(FloatWndService service) {
		super(service.getApplicationContext()); 
		this.manager = service.getManager();
		this.params = initParams(service.getApplicationContext());
		
		fetchWndSize();	
		install(true);
	}
	
	public WindowManager getManager() {
		return manager;
	}
	
	public WindowManager.LayoutParams getParams() {
		return params;
	}
	
	/**
	 *  获取悬浮窗尺寸
	 */
	private void fetchWndSize(){
		ViewTreeObserver vto = getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
			@Override
			public void onGlobalLayout() {
				height = FloatWindow.this.getHeight(); 
				width = FloatWindow.this.getWidth(); 
			}
		});
	}
	
	public int getWndWidth() {
		return width;
	}
	
	public int getWndHeight() {
		return height;
	}

	/**
	 * 初始化悬浮窗界面
	 */
	public abstract WindowManager.LayoutParams initParams(Context context);
	
	
	/**
	 * 安装和卸载
	 * @param flag
	 */
	public void install(boolean flag){
		if(flag){
			manager.addView(this, params); 
		} else {
			manager.removeView(this);
		}
		isInstalled = flag;
	}
	
	public boolean isInstalled() {
		return isInstalled;
	} 
	
	/**
	 * 工作和非工作状态转换
	 * @param visible
	 */
	public void setWorked(boolean isWorked){
		doWork(isWorked);
		this.isWorked = isWorked;
	} 

	public abstract void doWork(boolean isWorked);
	
	public boolean isWorked() {
		return isWorked;
	}
	
	public void setLocation(Point p){
		params.x = p.x;
		params.y = p.y;
		manager.updateViewLayout(this, params);
	}
	
	/**
	 * 设置悬浮窗可移动，并添加点击事件的监听器
	 * @param onClickListener
	 */
	@SuppressLint("ClickableViewAccessibility")
	public void canBeMoved(OnOperListener onOperateListener, int flag) {
		move_flag = flag;
		this.onOperateListener = onOperateListener; 
		setOnTouchListener(this);
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (action){
			case MotionEvent.ACTION_DOWN:
				isClick = true;
				lastX = (int) event.getRawX();
				lastY = (int) event.getRawY();
				paramX = params.x;
				paramY = params.y; 
				break;
			case MotionEvent.ACTION_MOVE:
				isClick = false;
				int dx = (int) event.getRawX() - lastX;
				int dy = (int) event.getRawY() - lastY; 
				if(move_flag == FLAG_MOVE_ALL){
					params.x = paramX + dx;
					params.y = paramY + dy;
				} else if(move_flag == FLAG_MOVE_X){
					params.x = paramX + dx;										
				} else if(move_flag == FLAG_MOVE_Y){
					params.y = paramY + dy;					
				}
				// 更新悬浮窗位置
				manager.updateViewLayout(this, params);
				break;
			case MotionEvent.ACTION_UP:
				int upX = (int) event.getRawX();
				int upY = (int) event.getRawY();
				if(isClick){
					isClick = false;
					if(onOperateListener != null){
						onOperateListener.onFloatWindowClick(this, new Point(upX, upY));
					}
				} else {
					if(onOperateListener != null){
						onOperateListener.onFloatWindowSwipe(this, 
								new Point(lastX, lastY), new Point(upX, upY));						
					}
				}
				break;
		}
		return true;
	}
	
}
