package com.jaly.touchscreenor.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.widget.EditText;
import android.widget.TimePicker;

/**
 * 任务参数设定
 * 
 * @author Administrator
 * 
 */
public class ParamSettingSteps {

	/**
	 * 从参数输入框中获取文本，并转换为参数键值对
	 * 
	 * @param content
	 * @return
	 */
	private static Map<String, String> getParamValue(String content) {
		Map<String, String> map = new HashMap<String, String>();
		String[] tokens = content.split("\n");
		for (String token : tokens) {
			if (token.contains("=")) {
				String[] tmp = token.split("=");
				map.put(tmp[0], tmp[1]);
			}
		}
		return map;
	}

	/**
	 * 从时间获取控件获取时分，并转换为标准的00:00格式
	 * 
	 * @param timePicker
	 * @return
	 */
	private static String getTimeString(int hour, int mini) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, mini);
		SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINA);
		return format.format(calendar.getTime());
	}

	/**
	 * 获取当前时间的标准格式
	 * 
	 * @return
	 */
	public static String getCurrentTimeString() {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINA);
		return format.format(new Date());
	}

	/**
	 * 显示参数输入框，并获取参数键值对
	 * 
	 * @param activity
	 * @param paramNames
	 * @return
	 */
	public static Map<String, String> showParamsDialog(Context context,
			Collection<String> paramNames) {
		if (paramNames == null || paramNames.isEmpty()) {
			return null;
		}
		Map<String, String> paramMap = new LinkedHashMap<String, String>();
		for (String paramName : paramNames) {
			paramMap.put(paramName, "");
		}
		return showParamsDialog(context, paramMap);
	}

	/**
	 * 显示参数设置对话框
	 * 
	 * @param context
	 * @param paramMap
	 * @return
	 */
	public static Map<String, String> showParamsDialog(Context context,
			Map<String, String> paramMap) {
		if (paramMap == null || paramMap.isEmpty()) {
			return null;
		} 
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : paramMap.entrySet()) {
			sb.append(entry.getKey()).append("=").append(entry.getValue())
			.append("\n");
		}
		sb.deleteCharAt(sb.length() - 1);
		
		EditText paramsEdit = new EditText(context);
		paramsEdit.setSingleLine(false);		
		paramsEdit.setText(sb.toString());
		paramsEdit.setSelection(sb.length());

		if (ComfirmDialog.showComfirmDialog(context, "设定脚本参数值", paramsEdit)) {
			String content = paramsEdit.getText().toString();
			return getParamValue(content);
		} else {
			return null;
		}
	}

	/**
	 * 显示时间设置对话框，并获取标准时间格式
	 * 
	 * @param activity
	 * @return
	 */
	public static String showTimerDialog(Context context) { 
		TimePicker timePicker = new TimePicker(context);		
		timePicker.setIs24HourView(true);
		if (ComfirmDialog.showComfirmDialog(context, "设置任务启动时间", timePicker)) {
			int hour = timePicker.getCurrentHour();
			int mini = timePicker.getCurrentMinute();
			return getTimeString(hour, mini);
		} else {
			return null;
		}
	}

}
