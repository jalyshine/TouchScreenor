package com.jaly.touchscreenor.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.jaly.touchscreenor.AppInfo;
import com.jaly.touchscreenor.exception.VsnMismatchingException;

public class AppUtils {

	public static final int FILTER_ALL_APP = 0;    // 所有应用程序
	public static final int FILTER_SYSTEM_APP = 1; // 系统程序
	public static final int FILTER_THIRD_APP = 2;  // 第三方应用程序
	public static final int FILTER_SDCARD_APP = 3; // 安装在SDCard的应用程序
	
	private static Map<String, Drawable> appIconMap = new HashMap<String, Drawable>();
	
	/**
	 * 建立文件名与图标的键值对
	 * @return
	 */
	public static void putAppIcon(String fileName, Drawable icon){
		if(appIconMap.get(fileName) == null){
			appIconMap.put(fileName, icon);
		}
	}
	
	public static Drawable getAppIcon(String fileName){
		return appIconMap.get(fileName);
	}

	/**
	 * 服务中显示通知
	 * @param message
	 */
	public static void serviceToast(final Context context, final String message){
		Handler handler=new Handler(Looper.getMainLooper());  
        handler.post(new Runnable(){
            public void run(){  
                Toast.makeText(context.getApplicationContext(), 
                		message, Toast.LENGTH_SHORT).show();  
            }  
        });
	}
	
	/**
	 * 判断应用是否具有某个权限
	 * @param context
	 * @return
	 */
	public static boolean hasPermission(Context context, String permission){ 
		PackageManager pm = context.getPackageManager();
		String packageName = context.getPackageName();
		int checkPermission = pm.checkPermission(permission, packageName);
        return PackageManager.PERMISSION_GRANTED == checkPermission; 
	}

	/**
	 * 根据包名和入口activity启动应用，并检查版本号是否与参数中的版本号一致
	 * @param context
	 * @param pkg 应用包名
	 * @param cls 入口activity类名（不带包名）
	 * @param vsn 应用版本号
	 * @throws NameNotFoundException
	 * @throws VsnMismatchingException 
	 */
	public static void startActivity(Context context, String pkg, String cls, int vsn) 
			throws NameNotFoundException, VsnMismatchingException{ 
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageInfo(pkg, 0);
		if(vsn == -1 || vsn == info.versionCode){
			Intent intent = new Intent();
			if(cls == null || "".equals(cls)){
				intent = pm.getLaunchIntentForPackage(pkg);
			} else {
				ComponentName cn = new ComponentName(pkg, pkg + cls);
				intent.setComponent(cn);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			context.startActivity(intent); 
		} else {
			throw new VsnMismatchingException("版本号不匹配，请更新脚本");
		}
	}
	
	/**
	 * 根据包名和入口activity启动应用
	 * @param context
	 * @param pkg
	 * @param cls
	 * @throws VsnMismatchingException 
	 * @throws NameNotFoundException 
	 */
	public static void startActivity(Context context, String pkg, String cls) 
			throws NameNotFoundException, VsnMismatchingException{
		startActivity(context, pkg, cls, -1);
	} 

	/**
	 * 获取正在运行的activity
	 * @param context
	 * @return
	 */
	public static String getRunningActivityName(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity
				.getClassName();
		return runningActivity;
	}

	/**
	 * 获取系统中的所有应用
	 * @param context
	 * @param filter
	 * @return
	 */
	public static List<AppInfo> queryAppInfo(Context context, int filter) {		
		List<AppInfo> mlistAppInfo = new ArrayList<AppInfo>();
		// 获得PackageManager对象
		PackageManager pm = context.getPackageManager(); 
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// 通过查询，获得所有ResolveInfo对象.
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent,
				PackageManager.GET_UNINSTALLED_PACKAGES);
		// 调用系统排序 ， 根据name排序。很重要，否则只能显示系统应用，而不能列出第三方应用程序
		Collections.sort(resolveInfos,
				new ResolveInfo.DisplayNameComparator(pm));

		for (ResolveInfo reInfo : resolveInfos) {
			String pkg = reInfo.activityInfo.packageName;
			PackageInfo packageInfo = null;
			int flags = 0; 
			try {
				packageInfo = pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES); 
				flags = packageInfo.applicationInfo.flags;
			} catch (NameNotFoundException e) {
				continue;
			} 
			boolean flag = false;
			
			switch (filter) {
				case FILTER_ALL_APP: // 所有应用程序 
					flag = true;
					break;
				case FILTER_SYSTEM_APP: // 系统程序  
					if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
						flag = true;
					} break;
				case FILTER_THIRD_APP: // 第三方应用程序   
					if ((flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {// 非系统程序
						flag = true;
					} else if ((flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
						// 本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
						flag = true;
					} break;
				case FILTER_SDCARD_APP: // 安装在SDCard的应用程序  
					if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
						flag = true;
					} break;
			} 
			if(flag){
				AppInfo appInfo = new AppInfo();
				appInfo.setAppLabel((String) reInfo.loadLabel(pm));
				appInfo.setPkgName(pkg);
				appInfo.setAppIcon(reInfo.loadIcon(pm));
				appInfo.setVsnCode(packageInfo.versionCode);
				List<String> activities = new ArrayList<String>();
				for(ActivityInfo info : packageInfo.activities){
					activities.add(info.name.replace(pkg, ""));
				}
				appInfo.setExportActivities(activities);
				mlistAppInfo.add(appInfo);
			} 
		}
		return mlistAppInfo;
	} 
	
}
