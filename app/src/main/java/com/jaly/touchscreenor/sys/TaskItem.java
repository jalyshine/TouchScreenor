package com.jaly.touchscreenor.sys;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.jaly.touchscreenor.coding.TagScript;

/**
 * 单项任务
 * @author Administrator
 *
 */
public class TaskItem implements Serializable{

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String fileName;           // 脚本文件名
	private String startTime;          // 启动时间（24小时制，格式**:**）
	private Long lStartTime;
	private TagScript tagScript;       // 解码后的脚本
	private Map<String, String> paramMap;
	private String paramString;
	private Boolean running = true;    // 任务状态 true-正在运行 false-停止运行

	public TaskItem() {

	}
	
	public TaskItem(Integer id, String fileName, Long lStartTime,
			String paraString, Boolean running) { 
		this.id = id;
		this.fileName = fileName;
		setlStartTime(lStartTime);
		setParamString(paraString);
		this.running = running;
	} 

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public TagScript getTagScript() {
		return tagScript;
	}

	public void setTagScript(TagScript tagScript) {
		this.tagScript = tagScript;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
		if(startTime != null){
			String[] times = startTime.split(":");
			int hour = Integer.parseInt(times[0]);
			int mini = Integer.parseInt(times[1]);
			
			Calendar calendar = Calendar.getInstance();			
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, mini);
			calendar.set(Calendar.SECOND, 0);
			
			this.lStartTime = calendar.getTimeInMillis();
		} else {
			this.lStartTime = 0L;   // 直接执行任务
		}
	}
	
	public Long getlStartTime() {
		return lStartTime;
	}

	public void setlStartTime(Long lStartTime) { 
		this.lStartTime = lStartTime;
		if(lStartTime != 0L){
			Date date = new Date(lStartTime);
			SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINA);
			startTime = format.format(date);			
		}
	}
	
	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
		if(paramMap != null){
			Set<String> keys = paramMap.keySet();
			Iterator<String> iterator = keys.iterator();
			StringBuilder sb = new StringBuilder(); 
			while(iterator.hasNext()){
				String key = iterator.next();
				String value = paramMap.get(key);
				sb.append(key).append("=").append(value).append("|");
			}
			sb.deleteCharAt(sb.length() - 1);
			paramString = sb.toString();
		}
	}

	public String getParamString() {
		return paramString;
	}

	public void setParamString(String paramString) {
		this.paramString = paramString;
		if(paramString != null){
			String[] tokens = paramString.split("\\|");
			paramMap = new HashMap<String, String>();
			for(String token : tokens){
				int idx = token.indexOf("="); 
				String key = token.substring(0, idx);
				String value = token.substring(idx + 1);
				paramMap.put(key, value);
			}
		}
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	@Override
	public String toString() {
		return "TaskItem [id=" + id + ", fileName=" + fileName + ", startTime="
				+ startTime + ", paramString=" + paramString + ", running="
				+ running + "]";
	}
	
}
