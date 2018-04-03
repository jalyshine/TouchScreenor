package com.jaly.touchscreenor.coding;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import android.view.KeyEvent;

/**
 * select标签
 * 
 * @author Administrator
 * 
 */ 
public class TagSelect extends TagOper{
	
	private static final long serialVersionUID = 1L;

	public enum Direct {
		left, right, up, down, ok, back
	} 
	
	private Direct direct;
	private int keyCode;

	public TagSelect() {
		this(Direct.left);
	}
	
	public TagSelect(Direct direct) { 
		setDirect(direct);
	}

	public Direct getDirect() {
		return direct;
	}

	public void setDirect(Direct direct) {
		this.direct = direct;
		switch(direct){
			case left: keyCode = KeyEvent.KEYCODE_DPAD_LEFT; break;
			case right: keyCode = KeyEvent.KEYCODE_DPAD_RIGHT; break;
			case up: keyCode = KeyEvent.KEYCODE_DPAD_UP; break;
			case down: keyCode = KeyEvent.KEYCODE_DPAD_DOWN; break;
			case ok: keyCode = KeyEvent.KEYCODE_DPAD_CENTER; break;
			case back: keyCode = KeyEvent.KEYCODE_BACK; break;
		}
	}
	
	public int getKeyCode() {
		return keyCode;
	}

	@Override
	public String getTagName() { 
		return TAG_SELECT;
	}
	
	@Override
	public Element makeElement(Document document) {
		Element select = super.makeElement(document);
		select.setAttribute(ATTR_DIRECT, direct.toString()); 
		return select;
	}
	
	@Override
	public void parse(Element element) {
		super.parse(element);
		NamedNodeMap attrs = element.getAttributes();
		
		Node node = attrs.getNamedItem(ATTR_DIRECT);
		if(node == null){
			return ;
		}
		setDirect(Direct.valueOf(node.getNodeValue()));
	}
	
	@Override
	protected String getShellCmd() {
		shellCmds = "input keyevent " + keyCode; 
		return super.getShellCmd();
	}
	
	@Override
	public String toString() { 
		return "<" + TAG_SELECT + " " 
				+ ATTR_DIRECT + "=\"" + direct + "\" " 
				+ ATTR_DESC + "=\"" + desc + "\" "
				+ ATTR_DELAY + "=\"" + delay + "\" />";
	}
}
