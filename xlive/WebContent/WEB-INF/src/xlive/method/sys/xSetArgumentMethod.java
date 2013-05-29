package xlive.method.sys;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.*;

import xlive.method.*;

public class xSetArgumentMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		String method_name="";
		Element parent_method=(Element)evaluate("ancestor::method", getMethodNode(), XPathConstants.NODE);
		if(parent_method != null) method_name=parent_method.getAttribute("name")+".";
		String name=getMethodAttribute("name");
		String value=getMethodAttribute("value");
		String cdata=getMethodAttribute("cdata");
		if(name != null && name.trim().length() > 0){
			if(value != null){
				if(value.startsWith("${") && value.endsWith("}")){
					String value_argument=value.substring(2, value.length()-1);
					value=getQNameArguments(value_argument);
				}
				if(value.startsWith("$A{") && value.endsWith("}")){
					String value_argument=value.substring(3, value.length()-1);
					value=getQNameArguments(method_name+value_argument);
				}
				if(value.startsWith("$MA{") && value.endsWith("}")){
					String value_argument=value.substring(4, value.length()-1);
					value=getQNameArguments(value_argument);
				}
				if(value.startsWith("$P{") && value.endsWith("}")){
					String value_argument=value.substring(3, value.length()-1);
					value=getProperties(value_argument);
				}
				try {
					Node name_node=(Node)this.getServiceContext().getArguments(name, XPathConstants.NODE);
					if(name_node!=null)name_node.setTextContent(value);
					else{
						Element[] name_nodes=this.createElements("arguments."+name, value);
						getServiceContext().argumentsOperation(name_nodes[0],"overwrite", true);
					}
				}catch(XPathExpressionException xee){
					xee.printStackTrace();
				}
			}else if(cdata != null){
				if(cdata.startsWith("${") && cdata.endsWith("}")){
					String value_argument=cdata.substring(2, cdata.length()-1);
					cdata=getQNameArguments(value_argument);
				}
				if(cdata.startsWith("$A{") && cdata.endsWith("}")){
					String value_argument=cdata.substring(3, cdata.length()-1);
					cdata=getQNameArguments(method_name+value_argument);
				}
				if(cdata.startsWith("$MA{") && cdata.endsWith("}")){
					String value_argument=cdata.substring(4, cdata.length()-1);
					cdata=getQNameArguments(value_argument);
				}
				if(cdata.startsWith("$P{") && cdata.endsWith("}")){
					String value_argument=cdata.substring(3, cdata.length()-1);
					cdata=getProperties(value_argument);
				}
				try {
					Node name_node=(Node)this.getServiceContext().getArguments(name, XPathConstants.NODE);
					if(name_node!=null){
						name_node.setTextContent("");
						name_node.appendChild(name_node.getOwnerDocument().createCDATASection(cdata));
					}else{
						Element[] name_nodes=this.createElements("arguments."+name,"");
						name_nodes[name_nodes.length-1].appendChild(name_nodes[name_nodes.length-1].getOwnerDocument().createCDATASection(cdata));
						getServiceContext().argumentsOperation(name_nodes[0],"overwrite", true);
					}
				}catch(XPathExpressionException xee){
					xee.printStackTrace();
				}
			}
		}
		return true;
	}
}
