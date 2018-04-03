package com.jaly.touchscreenor.coding;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface TagBase {
	
	public final static String TAG_SCRIPT = "script";
	public final static String TAG_ACTION = "action";
	public final static String TAG_CLICK = "click";
	public final static String TAG_SWIPE = "swipe";
	public final static String TAG_INPUT = "input";
	public final static String TAG_SELECT = "select"; 

	public final static String ATTR_PACKAGE = "package";
	public final static String ATTR_MAIN = "main";
	public final static String ATTR_VSNCODE = "vsncode";
	public final static String ATTR_DELAY = "delay"; 
	public final static String ATTR_DESC = "desc"; 
	public final static String ATTR_ACTIVITY = "activity";
	public final static String ATTR_POINT = "point";
	public final static String ATTR_LONG = "long";
	public final static String ATTR_STARTPOINT = "sp"; 
	public final static String ATTR_ENDPOINT = "ep"; 
	public final static String ATTR_CONTENT = "content";
	public final static String ATTR_DIRECT = "direct"; 

	public String getTagName();
	
	public Element makeElement(Document document);
	
	public void parse(Element element);

}
