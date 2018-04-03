package com.jaly.touchscreenor.coding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

/**
 * script标签
 * 
 * @author Administrator
 * 
 */
public class TagScript implements TagBase, Serializable{ 
	
	private static final long serialVersionUID = 1L;
	
	private String packageName;
	private String mainActivity;
	private int delay;
	private int vsnCode; 
	
	private List<TagAction> actions; 
	private Map<String, TagInput> paramMap; 

	public TagScript() {
		actions = new ArrayList<TagAction>();
		paramMap = new HashMap<String, TagInput>();		
	}
	
	public TagScript(String packageName, 
			String mainActivity, int vsnCode, int delay) {
		this(); 
		this.packageName = packageName; 
		this.vsnCode = vsnCode;
		this.delay = delay; 
		setMainActivity(mainActivity);
	} 

	public String getPkgName() {
		return packageName;
	}

	public void setPkgName(String pkgName) {
		this.packageName = pkgName;
	}
	
	public String getMainActivity() {
		return mainActivity;
	}

	public void setMainActivity(String mainActivity) {
		if(mainActivity == null){
			mainActivity = "";
		}
		this.mainActivity = mainActivity;
	}

	public int getVsnCode() {
		return vsnCode;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public List<TagAction> getActions() {
		return actions;
	}

	public Map<String, TagInput> getParamMap() {
		return paramMap;
	}

	@Override
	public String getTagName() { 
		return TAG_SCRIPT;
	}
	
	/**
	 * 注入参数值
	 * @param map
	 */
	public void injectParamValue(Map<String, String> map){ 
		for(String key : paramMap.keySet()){
			String value = map.get(key);
			if(value != null){
				paramMap.get(key).setContent(value); 
			}
		} 
	} 
	
	@Override
	public Element makeElement(Document document) { 
		Element root = document.createElement(getTagName());
		root.setAttribute(ATTR_PACKAGE, packageName);
		root.setAttribute(ATTR_MAIN, mainActivity);
		root.setAttribute(ATTR_VSNCODE, String.valueOf(vsnCode));
		root.setAttribute(ATTR_DELAY, String.valueOf(delay)); 
		
		if(actions != null){
			for(TagAction action : actions){
				Element element = action.makeElement(document);
				root.appendChild(element);
			}
		}
		
		document.appendChild(root);
		return root;
	}

	@Override
	public void parse(Element element) {
		NamedNodeMap attrs = element.getAttributes(); 
		// 包名
		Node node = attrs.getNamedItem(ATTR_PACKAGE);
		if(node != null){
			packageName = node.getNodeValue();
		} 
		// 版本号
		node = attrs.getNamedItem(ATTR_MAIN);
		if(node != null){ 
			mainActivity = node.getNodeValue();
		}
		// 版本号
		node = attrs.getNamedItem(ATTR_VSNCODE);
		if(node != null){
			try {
				vsnCode = Integer.parseInt(node.getNodeValue());
			} catch (Exception e) {
				Log.e("TagScript", e.getMessage());
			}
		}
		// 延迟时间
		node = attrs.getNamedItem(ATTR_DELAY);
		if(node != null){
			try {
				delay = Integer.parseInt(node.getNodeValue());
			} catch (Exception e) {
				Log.e("TagScript", e.getMessage());
			}
		}
		// 解析子节点
		NodeList childs = element.getChildNodes();
		for(int i=0; i<childs.getLength(); i++){
			Node child = childs.item(i);
			if(!TAG_ACTION.equals(child.getNodeName())){
				continue;
			}
			TagAction tagAction = new TagAction();
			tagAction.parse((Element)child);
			actions.add(tagAction);
			paramMap.putAll(tagAction.getParamMap());
		}
	} 
	
	@Override
	public String toString() { 
		return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<" 
		+ TAG_SCRIPT + " " 
		+ ATTR_PACKAGE + "=\"" + packageName + "\" \n " 
		+ ATTR_MAIN + "=\"" + mainActivity + "\" \n " 
		+ ATTR_VSNCODE + "=\"" + vsnCode + "\" \n " 
		+ ATTR_DELAY + "=\"" + delay + "\">\n\n</" 
		+ TAG_SCRIPT + ">"; 
	}

}
