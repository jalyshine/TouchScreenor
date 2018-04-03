package com.jaly.touchscreenor.coding;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * swipe标签
 * @author Administrator
 *
 */
public class TagSwipe extends TagOper{ 
	
	private static final long serialVersionUID = 1L;
	
	private String startPoint;
	private String endPoint; 

	public TagSwipe() {
		this("", "");
	}
	
	public TagSwipe(String startPoint, String endPoint) { 
		this.startPoint = startPoint;
		this.endPoint = endPoint;
	}

	public String getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(String startPoint) {
		this.startPoint = startPoint;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	} 
	
	@Override
	public String getTagName() { 
		return TAG_SWIPE;
	}
	
	@Override
	public Element makeElement(Document document) {
		Element swipe = super.makeElement(document);
		swipe.setAttribute(ATTR_STARTPOINT, startPoint);
		swipe.setAttribute(ATTR_ENDPOINT, endPoint); 
		return swipe;
	}
	
	@Override
	public void parse(Element element) {
		super.parse(element);
		NamedNodeMap attrs = element.getAttributes();
		
		Node sp = attrs.getNamedItem(ATTR_STARTPOINT);
		Node ep = attrs.getNamedItem(ATTR_ENDPOINT);
		if(sp == null || ep == null){
			return ;
		} 
		startPoint = sp.getNodeValue();
		endPoint = ep.getNodeValue(); 
	}
	
	@Override
	protected String getShellCmd() {
		shellCmds = "input swipe " + startPoint + " " + endPoint;
		return super.getShellCmd();
	}
	
	@Override
	public String toString() { 
		return "<" + TAG_SWIPE + " " 
				+ ATTR_STARTPOINT + "=\"" + startPoint + "\" " 
				+ ATTR_ENDPOINT + "=\"" + endPoint + "\" " 
				+ ATTR_DESC + "=\"" + desc + "\" "
				+ ATTR_DELAY + "=\"" + delay + "\" />";
	}
}
