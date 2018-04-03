package com.jaly.touchscreenor.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 尺寸相关工具
 * @author Administrator
 *
 */
public class DensityUtils {  
	
	/**
	 * 获取状态栏高度
	 * @return
	 */
	public static int getStatusBarHeight(Context context){
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if(resourceId > 0){
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
	
	/**
	 * 获取屏幕尺寸
	 * @param context
	 * @return
	 */
	public static DisplayMetrics getScreenSize(Context context){ 
		return context.getResources().getDisplayMetrics();  
	}
	
    /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
}  