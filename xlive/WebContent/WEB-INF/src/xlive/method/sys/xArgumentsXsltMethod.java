package xlive.method.sys;

import xlive.method.*;
import xlive.xml.xXmlDocument;

import javax.xml.xpath.XPathConstants;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;


public class xArgumentsXsltMethod extends xDefaultMethod{
	public Object process() throws xMethodException {
		try{
			Node arguments_node=(Node)getQNameArguments("./*[1]", XPathConstants.NODE);
			arguments_node=arguments_node.getParentNode();
			DOMSource source_xml= new DOMSource(arguments_node.cloneNode(true));
			//
			Element stylesheet= createElement("xsl:stylesheet");
			stylesheet.setAttribute("xmlns:xsl", "http://www.w3.org/1999/XSL/Transform");
			stylesheet.setAttribute("version", "1.0");
			Node node=getMethodNode().getFirstChild();
			while(node != null){
				if("xsl:stylesheet".equals(node.getNodeName())) continue;
				stylesheet.appendChild(node.cloneNode(true));
				node=node.getNextSibling();
			}
			String xslt= xXmlDocument.nodeToString(stylesheet, null);
			/*
			String xslt=
				"<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"+
				"<xsl:template match=\"/\">"+
				"<xsl:copy-of select=\".\"/>"+
				"</xsl:template>"+
				"</xsl:stylesheet>";

			*/
			javax.xml.transform.stream.StreamSource source_xsl= new javax.xml.transform.stream.StreamSource(new java.io.StringReader(xslt));
			//javax.xml.transform.dom.DOMSource source_xsl= new javax.xml.transform.dom.DOMSource(stylesheet);
			
			Transformer transform=TransformerFactory.newInstance().newTransformer(source_xsl);
			DOMResult dom_result=new DOMResult();
			transform.transform(source_xml, dom_result);
			//
			//update arguments
			//
		}catch(TransformerConfigurationException tcx){
			tcx.printStackTrace();
		}catch(TransformerException te){
			te.printStackTrace();
		}
		return getServiceContext().doNextProcess();
	}
}
