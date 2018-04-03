package com.jaly.touchscreenor.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.jaly.touchscreenor.input.ImeService;

import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 输入法相关工具
 * 
 * @author Administrator
 * 
 */
public class InputUtils {

	/**
	 * 获取系统所有输入法
	 * 
	 * @param isEnabled
	 *            ：true可用，false所有
	 * @return app名称和输入法id的键值对
	 */
	public static Map<String, String> listAllIME(Context context,
			boolean isEnabled) {
		Map<String, String> res = new LinkedHashMap<String, String>();
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<InputMethodInfo> list = isEnabled ? imm
				.getEnabledInputMethodList() : imm.getInputMethodList();
		for (InputMethodInfo info : list) {
			String id = info.getId();
			String appLabel = info.getServiceInfo().loadLabel(pm).toString();
			res.put(appLabel, id);
		}
		return res;
	}

	/**
	 * 获取当前输入法的ID
	 * 
	 * @return
	 */
	public static String getCurIME(Context context) {
		String ime = Settings.Secure.getString(context.getContentResolver(),
				Settings.Secure.DEFAULT_INPUT_METHOD);
		return ime;
	}

	/**
	 * 设置默认输入法
	 * 
	 * @param context
	 * @param id
	 */
	public static void switchIME(Context context, String id) {
		if (id == null) {
			String pkg = context.getPackageName();
			id = ImeService.class.getName().replace(pkg, pkg + "/");
		}
		Settings.Secure.putString(context.getContentResolver(),
				Settings.Secure.DEFAULT_INPUT_METHOD, id);
	}

	/**
	 * 隐藏&显示虚拟键盘
	 * 
	 * @param v
	 * @param show
	 *            true显示false隐藏
	 */
	public static void ShowKeyboard(View v, boolean show) {
		InputMethodManager imm = (InputMethodManager) v.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (show) {
			imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
		} else if (imm.isActive()) {
			imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
		}
	}

	/**
	 * 强制显示&隐藏虚拟键盘
	 * 
	 * @param txtSearchKey
	 * @param isShow
	 */
	public static void Keyboard(final EditText editText, final boolean show) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager m = (InputMethodManager) editText
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				if (show) {
					m.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
				} else {
					m.hideSoftInputFromWindow(editText.getWindowToken(), 0);
				}
			}
		}, 300);
	}

	/**
	 * 判断虚拟键盘是否打开
	 * 
	 * @param editText
	 * @return
	 */
	public static boolean IsKeyboardOpen(EditText editText) {
		InputMethodManager imm = (InputMethodManager) editText.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		return imm.isActive();
	}

	/**
	 * Unicode转中文
	 * @param unicode
	 * @return
	 */
	public static String fromUnicode(String unicode) {
		String[] strs = unicode.split("\\\\u");
		String returnStr = "";
		for (int i = 1; i < strs.length; i++) {
			returnStr += (char) Integer.valueOf(strs[i], 16).intValue();
		}
		return returnStr;
	}

	/**
	 * 中文转Unicode
	 * @param cn
	 * @return
	 */
	public static String toUnicode(String cn) {
		char[] chars = cn.toCharArray();
		String returnStr = "";
		for (int i = 0; i < chars.length; i++) {
			returnStr += "\\u" + Integer.toString(chars[i], 16);
		}
		return returnStr;
	}
	
}
