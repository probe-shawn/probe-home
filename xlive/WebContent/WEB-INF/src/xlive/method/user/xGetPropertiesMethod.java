package xlive.method.user;


import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Node;

import xlive.method.*;
import xlive.xml.xXmlDocument;

public class xGetPropertiesMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		String hash_code=generateHashCode();
		//setProperties("login.hash", hash_code);
		Node clone_properties= getObjectPropertiesNode().cloneNode(true);
		Node exclude_node=(Node)getArguments("exclude",XPathConstants.NODE);
		if(exclude_node != null)xXmlDocument.extendNodes(clone_properties, exclude_node, "remove");
		Node return_properties=setMethodReturnArguments("", "properties", "");
		return_properties.getParentNode().replaceChild(clone_properties, return_properties);
		setMethodReturnArguments("", "properties.login.hash", hash_code);
		return true;
	}
	private String generateHashCode(){
		org.apache.commons.codec.binary.Base64 encode = new org.apache.commons.codec.binary.Base64();
		//sun.misc.BASE64Encoder encode=new sun.misc.BASE64Encoder();
		String hash=new String(encode.encode(java.util.UUID.randomUUID().toString().getBytes()));
		return hash.substring(hash.length()-4);
	}
}
