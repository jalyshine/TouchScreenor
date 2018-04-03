package com.jaly.touchscreenor.coding;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import android.util.Log;

/**
 * 操作标签的基类
 * @author Administrator
 *
 */
public class TagOper implements TagBase, Serializable{
	
	private static final long serialVersionUID = 1L;
	
	protected int id;
	public int delay;
	public String desc = "";
	protected String shellCmds;
	protected String activity;
	
	public TagOper() {
		
	}

	protected int getId() {
		return id;
	}

	protected void setId(int id) {
		this.id = id;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String getTagName() { 
		return null;
	}

	@Override
	public Element makeElement(Document document) {
		Element element = document.createElement(getTagName());
		element.setAttribute(ATTR_DELAY, String.valueOf(delay));
		element.setAttribute(ATTR_DESC, desc);
		return element;
	}

	@Override
	public void parse(Element element) {
		NamedNodeMap attrs = element.getAttributes();
		Node node = attrs.getNamedItem(ATTR_DESC);
		if(node != null){
			desc = node.getNodeValue();			
		}
		node = attrs.getNamedItem(ATTR_DELAY);
		if(node != null){
			try {
				delay = Integer.parseInt(node.getNodeValue());
			} catch (Exception e) { 
				Log.e("TagOper", e.getMessage());
			}
		} 
	}

	protected String getShellCmd() {
		return shellCmds;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	} 
	
}
