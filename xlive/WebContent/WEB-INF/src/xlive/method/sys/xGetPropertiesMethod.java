package xlive.method.sys;

import javax.xml.xpath.XPathConstants;

import xlive.method.*;
import org.w3c.dom.*;
import xlive.xml.*;

public class xGetPropertiesMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		extendDefaultPropertiesToArguments();
		Node clone_properties= getObjectPropertiesNode().cloneNode(true);
		Node exclude_node=(Node)getArguments("exclude",XPathConstants.NODE);
		if(exclude_node != null)xXmlDocument.extendNodes(clone_properties, exclude_node, "remove");
		Node return_properties=setMethodReturnArguments("", "properties", "");
		return_properties.getParentNode().replaceChild(clone_properties, return_properties);
		return true;
	}
}
