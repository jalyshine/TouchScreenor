package com.jaly.touchscreenor.coding;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import android.graphics.Point;

import com.jaly.touchscreenor.coding.TagSelect.Direct;
import com.jaly.touchscreenor.sys.AppSetting;
import com.jaly.touchscreenor.sys.AppSettingManager;
import com.jaly.touchscreenor.util.CommandExecution;

/**
 * 脚本编码操作
 * 
 * @author Administrator
 * 
 */
public class ScriptEncoding {

	private Document document;
	private TagScript tagScript;
	private CommandExecution ce;
	private List<TagOper> operBuffer;
	private String curActivity;
	private TagOper lastOper;
	
	private int operDelay;
	private boolean isUnicode;

	public ScriptEncoding(String pkgName, String mainActivity, int vsnCode, CommandExecution ce)
			throws ParserConfigurationException {
		this.ce = ce;
		operBuffer = new ArrayList<TagOper>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.newDocument(); 
		// 读取系统设置，默认延时
		AppSetting setting = AppSettingManager.getInstance().getCurSetting();
		isUnicode = setting.isUnicode();
		operDelay = setting.getOperDefDelay();
		int defDelay = setting.getAppDefDelay(); 
		tagScript = new TagScript(pkgName, mainActivity, vsnCode, defDelay);
	}

	/**
	 * 重置tagScript内容
	 */
	public void reset() {
		tagScript.getActions().clear();
	}

	/**
	 * 设置操作描述
	 * @param desc
	 * @param delay
	 */
	public void setDescription(String desc) {
		if(lastOper != null){
			lastOper.setDesc(desc);
		}
	}
	
	/**
	 * 添加动作
	 * @param activity
	 * @param oper
	 */
	private void append(TagOper oper){
		String activity = oper.getActivity();
		if(curActivity != null && curActivity.equals(activity)){
			int len = tagScript.getActions().size();
			tagScript.getActions().get(len - 1).getOpers().add(oper);
		} else {
			TagAction action = new TagAction(activity);
			action.getOpers().add(oper);
			tagScript.getActions().add(action);
		}
		lastOper = oper;
		curActivity = activity;
	}
	
	/**
	 * 移除最后一个操作
	 * @return
	 */
	public TagOper remove(){
		if(tagScript.getActions() == null || tagScript.getActions().isEmpty()){ 
			return null;
		}
		int actionSize = tagScript.getActions().size(); 
		TagAction action = tagScript.getActions().get(actionSize - 1);
		int len = action.getOpers().size(); 
		TagOper tagOper = action.getOpers().get(len - 1);
		action.getOpers().remove(len - 1);
		if(len == 1){
			tagScript.getActions().remove(actionSize - 1); 
		}
		return tagOper;
	}
	
	/**
	 * 撤销一次操作
	 */
	public String undo(){
		TagOper oper = remove();
		if(oper == null){
			return null;
		}
		operBuffer.add(oper);
		return oper.getDesc();
	}
	
	/**
	 * 重做一次操作
	 */
	public String redo(){
		if(operBuffer.isEmpty()){
			return null;
		}
		TagOper oper = operBuffer.get(0);
		append(oper);
		operBuffer.remove(0);
		return oper.getDesc();
	}

	/**
	 * 添加tagClick
	 * 
	 * @param activity
	 * @param point
	 * @param isShellEnabled
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public void addClick(String activity, Point pos, boolean isShellEnabled) 
			throws IOException, InterruptedException {
		String posStr = pos.x + " " + pos.y;
		TagClick click = new TagClick(posStr, 0);
		click.setDelay(operDelay);
		click.setActivity(activity);
		append(click);
		if (isShellEnabled) { 
			ce.execShellCmd(click.getShellCmd()); 
		}
	}

	/**
	 * 添加tagSwipe
	 * 
	 * @param activity
	 * @param start
	 * @param end
	 * @param isShellEnabled
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public void addSwipe(String activity, Point start, Point end,
			boolean isShellEnabled) throws IOException, InterruptedException {
		String startStr = start.x + " " + start.y;
		String endStr = end.x + " " + end.y;
		TagSwipe swipe = new TagSwipe(startStr, endStr);
		swipe.setDelay(operDelay);
		swipe.setActivity(activity);
		append(swipe);
		if (isShellEnabled) { 
			ce.execShellCmd(swipe.getShellCmd()); 
		}
	}

	/**
	 * 添加tagInput
	 * 
	 * @param activity
	 * @param content
	 * @param isShellEnabled
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public void addInput(String activity, String content, boolean isShellEnabled)
			throws IOException, InterruptedException {
		TagInput input = new TagInput(content, isUnicode);
		input.setActivity(activity);
		input.setDelay(operDelay);
		append(input);
		if (isShellEnabled) { 
			ce.execShellCmd(input.getShellCmd());  
		}
	} 

	/**
	 * 添加tagSelect
	 * 
	 * @param activity
	 * @param direct
	 * @param isShellEnabled
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public void addSelect(String activity, Direct direct, boolean isShellEnabled) 
			throws IOException, InterruptedException {
		TagSelect select = new TagSelect(direct);
		select.setActivity(activity);
		select.setDelay(operDelay);
		append(select);
		if (isShellEnabled) { 
			ce.execShellCmd(select.getShellCmd()); 
		}
	} 

	public String write() throws TransformerException {
		// 创建TransformerFactory对象
		TransformerFactory tff = TransformerFactory.newInstance();
		// 创建Transformer对象
		Transformer tf = tff.newTransformer();
		// 设置输出数据时换行
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		// 设置缩进为2
		tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		// 将tagScript中的代码写入document
		tagScript.makeElement(document);
		// 使用Transformer的transform()方法将DOM树转换成XML
		DOMSource source = new DOMSource(document); 
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer); 
		tf.transform(source, result); 
		
		return writer.toString();
	} 

}