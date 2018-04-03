package com.jaly.touchscreenor.coding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * action标签类
 * 
 * @author Administrator
 * 
 */
public class TagAction implements TagBase, Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String activity;
	private List<TagOper> opers;
	private Map<String, TagInput> paramMap;

	public TagAction() {
		activity = "";
		paramMap = new HashMap<String, TagInput>();
		opers = new ArrayList<TagOper>();
	}
	
	public TagAction(String activity) {
		this();
		this.activity = activity; 
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	} 
	
	public List<TagOper> getOpers() {
		return opers;
	}
	
	public Map<String, TagInput> getParamMap() {
		return paramMap;
	} 
	
	/**
	 * 获取shell命令集
	 * @return
	 */
	public List<String> getShellCmds() {
		List<String> shellCmds = new ArrayList<String>();
		for(TagOper oper : opers){
			shellCmds.add(oper.getShellCmd());
			if(oper.getDelay() > 0){
				shellCmds.add("delay " + oper.getDelay());
			}
		}
		return shellCmds;
	}

	@Override
	public String getTagName() { 
		return TAG_ACTION;
	}

	@Override
	public Element makeElement(Document document) { 
		Element action = document.createElement(getTagName());
		action.setAttribute(ATTR_ACTIVITY, activity); 
		if(opers != null){
			for(TagOper oper : opers){
				action.appendChild(oper.makeElement(document));
			}
		}
		return action;
	}

	@Override
	public void parse(Element element) {
		Node node = element.getAttributes().getNamedItem(ATTR_ACTIVITY);
		if(node == null){
			return ;
		}
		activity = node.getNodeValue();
		
		NodeList cmds = element.getChildNodes(); 
		for(int j=0; j<cmds.getLength(); j++){
			Node cmd = cmds.item(j);
			if(cmd.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			String tagName = cmd.getNodeName();
			TagOper oper = null;
			if(TAG_CLICK.equals(tagName)){
				oper = new TagClick();
			} else if(TAG_SWIPE.equals(tagName)){
				oper = new TagSwipe();				
			} else if(TAG_INPUT.equals(tagName)){
				oper = new TagInput();
			} else if(TAG_SELECT.equals(tagName)){
				oper = new TagSelect();
			}
			if(oper != null){
				oper.parse((Element)cmd);
				if(oper instanceof TagInput){
					String paramName = ((TagInput)oper).getParamName();
					if(paramName != null){
						paramMap.put(paramName, (TagInput)oper);
					}
				}
				opers.add(oper);
			}
		}
	}

	@Override
	public String toString() { 
		return "<" + TAG_ACTION + " " 
				+ ATTR_ACTIVITY + "=\"" + activity + "\">\n\n</" 
				+ TAG_ACTION + ">"; 
	}
	
}
