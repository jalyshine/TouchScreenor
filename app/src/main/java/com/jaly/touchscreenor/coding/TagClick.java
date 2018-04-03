package com.jaly.touchscreenor.coding;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import android.util.Log;

/**
 * click标签类
 * 
 * @author Administrator
 * 
 */
public class TagClick extends TagOper { 
	
	private static final long serialVersionUID = 1L;
	
	private String point;  //格式"x y"
	private int length; 

	public TagClick() {
		this("", 0);
	}
	
	public TagClick(String point, int length) { 
		this.point = point;
		this.length = length;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	} 
	
	@Override
	public String getTagName() {
		return TAG_CLICK;
	}

	@Override
	public Element makeElement(Document document) {
		Element click = super.makeElement(document);
		click.setAttribute(ATTR_POINT, point);
		click.setAttribute(ATTR_LONG, String.valueOf(length)); 
		return click;
	}
	
	@Override
	public void parse(Element element) {
		super.parse(element);
		NamedNodeMap attrs = element.getAttributes();
		
		Node node = attrs.getNamedItem(ATTR_POINT);
		if(node == null){
			return ;
		}
		point = node.getNodeValue();
		
		node = attrs.getNamedItem(ATTR_LONG);
		if(node != null){
			try {
				length = Integer.parseInt(node.getNodeValue());
			} catch (Exception e) { 
				Log.e("TagClick", e.getMessage());
			}
		}
	}
	
	@Override
	protected String getShellCmd() {		
		if(length == 0){  // 点击 
			shellCmds = "input tap " + point; 
		} else {          // 长按
			shellCmds = "input swipe " + point + " " + point + " " + length;
		}
		return super.getShellCmd();
	}
	
	@Override
	public String toString() { 
		return  "<" + TAG_CLICK + " "
				+ ATTR_POINT + "=\"" + point + "\" " 
				+ ATTR_LONG + "=\"" + length + "\" "
				+ ATTR_DESC + "=\"" + desc + "\" "
				+ ATTR_DELAY + "=\"" + delay + "\" />";
	}
}
