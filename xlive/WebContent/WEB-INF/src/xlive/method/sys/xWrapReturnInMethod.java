package xlive.method.sys;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.*;

import xlive.method.*;

public class xWrapReturnInMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		try{
			String name=getMethodAttribute("name");
			Element[] name_nodes=this.createElements(name);
			Node return_node=(Node)this.getServiceContext().getArguments("./return", XPathConstants.NODE);
			Node node=return_node.getFirstChild();
			while(node!=null){
				if(node.getNodeType()==Node.ELEMENT_NODE &&((Element)node).getAttributeNode("wrapReturnIn")!=null) {
					node=node.getNextSibling();
				}else{
					Node wrap_in=node;
					node=node.getNextSibling();
					name_nodes[name_nodes.length-1].appendChild(wrap_in);
				}
			}
			return_node.appendChild(name_nodes[0]);
			name_nodes[0].setAttribute("wrapReturnIn", "");
			return true;
		}catch(XPathExpressionException xee){
			xee.printStackTrace();
		}
		return false;
	}
}
