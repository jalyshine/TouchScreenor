package com.jaly.touchscreenor.coding;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.jaly.touchscreenor.input.ImeService;
import com.jaly.touchscreenor.util.InputUtils;

/**
 * input标签
 * @author Administrator
 *
 */
public class TagInput extends TagOper{
	
	private static final long serialVersionUID = 1L; 
	
	private String content;
	private String paramName;
	private boolean isUnicode;

	public TagInput() {
		content = "";
		paramName = "";
	}
	
	public TagInput(String content, boolean isUnicode) {
		this();
		this.isUnicode = isUnicode;
		setContent(content);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
		if(content.startsWith("${") && content.endsWith("}")){
			paramName = content.substring(2, content.length() - 1);
		}
	} 

	public String getParamName() {
		return paramName;
	}
	
	@Override
	public String getTagName() {
		return TAG_INPUT;
	}
	
	@Override
	public Element makeElement(Document document) {
		Element input = super.makeElement(document);
		input.setAttribute(ATTR_CONTENT, content); 
		return input;
	}
	
	@Override
	public void parse(Element element) {
		super.parse(element);
		NamedNodeMap attrs = element.getAttributes();
		
		Node node = attrs.getNamedItem(ATTR_CONTENT);
		if(node == null){
			return ;
		}
		setContent(node.getNodeValue()); 
	}
	
	@Override
	protected String getShellCmd() {
		if(isUnicode){
			shellCmds = "am broadcast -a " + ImeService.ACTION_REPLACE
					+ " --es " + ImeService.EXTRA_CONTENT 
					+ " '" + InputUtils.toUnicode(content) + "'";			
		} else {
			shellCmds = "input text " + content;
		}
		return super.getShellCmd();
	}
	
	@Override
	public String toString() { 
		return "<" + TAG_INPUT + " " 
				+ ATTR_CONTENT + "=\"" + content + "\" "
				+ ATTR_DESC + "=\"" + desc + "\" "
				+ ATTR_DELAY + "=\"" + delay + "\" />";
	}
}
