package com.jaly.touchscreenor.coding;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 脚本解码
 * @author Administrator
 *
 */
public class ScriptDecoding {

	private TagScript tagScript = null;
	private Element root;
	
	public ScriptDecoding(String filePath)
			throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
		DocumentBuilder builder = factory.newDocumentBuilder(); 
		//必须加上“file://”协议头，否则报错Protocol not found 
		Document document = builder.parse("file://" + filePath);
		if(document != null){
			NodeList nodes = document.getElementsByTagName(TagBase.TAG_SCRIPT);
			if(nodes.getLength() == 1){
				root = (Element)nodes.item(0);
				tagScript = new TagScript();
				tagScript.parse(root);
			}
		}
	} 	
	
	public TagScript getTagScript() {
		return tagScript;
	}
	
}
