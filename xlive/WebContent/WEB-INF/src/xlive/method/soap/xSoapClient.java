package xlive.method.soap;

import java.io.IOException;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPFault;


import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class xSoapClient {
	
	public static final String XSI_NAMESPACE_PREFIX = "xsi";
	public static final String XSI_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema-instance";
	public static final String XSD_NAMESPACE_PREFIX = "xsd";
	public static final String XSD_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema";
	private String defaultNamespacePrefix="tns";
	private String defaultNamespaceURI=null;
	private String schemaElementFormDefault=null;
	private boolean elementFormDefault=false;
	
	private boolean dumpRequestMessage=false;
	private boolean dumpResponse=false;
	private Document defaultDocument=null;
	
	private SOAPFault resultFault=null;
	
	public xSoapClient(){}
	public void setElementFormDefault(String qualified){
		schemaElementFormDefault=qualified;
	}
	public void setDefaultNamespacePrefix(String ns){
		defaultNamespacePrefix=ns;
	}
	public void setDefaultNamespaceURI(String uri){
		defaultNamespaceURI=uri;
	}
	public void setDumpRequestMessage(boolean value){
		dumpRequestMessage=value;
	}
	public void setDumpResponse(boolean value){
		dumpResponse=value;
	}
	public void setDefaultDocument(Document default_document){
		defaultDocument=default_document;
	}
	public Node invokeOperation(xOperationInfo operation) throws UnsupportedOperationException, SOAPException, ParserConfigurationException, MalformedURLException{
	
			boolean is_rpc="rpc".equalsIgnoreCase(operation.getSoapOperationStyle());
			if(defaultNamespaceURI==null) defaultNamespaceURI=operation.getTargetNamespace();
			if(schemaElementFormDefault==null) schemaElementFormDefault=operation.getSchemaElementFormDefault();
			elementFormDefault="qualified".equalsIgnoreCase(schemaElementFormDefault);
			//
			SOAPConnection connection = SOAPConnectionFactory.newInstance().createConnection();
			SOAPMessage message = MessageFactory.newInstance().createMessage();
			SOAPPart soap_part = message.getSOAPPart();
			SOAPEnvelope envelope = soap_part.getEnvelope();
			envelope.addNamespaceDeclaration(XSI_NAMESPACE_PREFIX, XSI_NAMESPACE_URI);
			envelope.addNamespaceDeclaration(XSD_NAMESPACE_PREFIX, XSD_NAMESPACE_URI);
			SOAPHeader header = envelope.getHeader();
			Element input_header = operation.getInputHeaderMessage();
			if(input_header==null) header.detachNode();
			else{
				if(elementFormDefault) header.addNamespaceDeclaration(defaultNamespacePrefix, defaultNamespaceURI);
				if(input_header!=null){
					NodeList children=input_header.getChildNodes();
					for(int i=0;i<children.getLength();++i){
						if(children.item(i).getNodeType() != Node.ELEMENT_NODE)continue;
						Element child_element = (Element)children.item(i);
						SOAPElement soap_child_element;
						if(elementFormDefault){
							Name child_name = envelope.createName(child_element.getNodeName(), defaultNamespacePrefix, defaultNamespaceURI);
							soap_child_element= header.addChildElement(child_name);
						}else soap_child_element = header.addChildElement(child_element.getNodeName());
						dom2SoapElement(envelope, soap_child_element, child_element,is_rpc);
					}
				}
			}
			SOAPBody body = envelope.getBody();
			if(elementFormDefault) body.addNamespaceDeclaration(defaultNamespacePrefix, defaultNamespaceURI);
			//input
			Element input_element = operation.getInputMessage();
			///
			if(input_element!=null){
				NodeList children=input_element.getChildNodes();
				for(int i=0;i<children.getLength();++i){
					if(children.item(i).getNodeType() != Node.ELEMENT_NODE)continue;
					Element child_element = (Element)children.item(i);
					SOAPElement soap_child_element;
					if(elementFormDefault){
						Name child_name = envelope.createName(child_element.getNodeName(), defaultNamespacePrefix, defaultNamespaceURI);
						soap_child_element= body.addChildElement(child_name);
					}else soap_child_element = body.addChildElement(child_element.getNodeName());
					if(is_rpc){
						Name child_name = envelope.createName(child_element.getNodeName(),"", operation.getInputSoapBodyNamespace());
						soap_child_element= body.addChildElement(child_name);
						soap_child_element.setEncodingStyle(operation.getInputSoapBodyEncodingStyle());
					}
					dom2SoapElement(envelope, soap_child_element, child_element,is_rpc);
				}
			}
			//
			String soap_action = operation.getSoapOperationSoapAction();
			if(soap_action != null && soap_action.length() > 0){
				MimeHeaders mimeHeaders = message.getMimeHeaders();
				mimeHeaders.setHeader("SOAPAction", "\"" + soap_action + "\"");
			}
			message.saveChanges();
			resultFault=null;
			URL endpoint=null;
			endpoint = new URL(operation.getSoapAddressLocation());
			if(dumpRequestMessage){
				ByteArrayOutputStream message_stream = new ByteArrayOutputStream();
				try {
					message.writeTo(message_stream);
				} catch (IOException e) {
					e.printStackTrace();
				}
				//System.out.println("SOAP Message Target URL: " + endpoint.toString());
				//System.out.println("SOAP Request: " + message_stream.toString());
			}
			SOAPMessage response = connection.call(message, endpoint);
			connection.close();
			if(response != null){
				if(dumpResponse){
					Source response_content = response.getSOAPPart().getContent();
					try {
						ByteArrayOutputStream message_stream = new ByteArrayOutputStream();
						TransformerFactory.newInstance().newTransformer().transform(response_content, new StreamResult(message_stream));
						//System.out.println("SOAP Response : " + message_stream.toString());
					}catch(TransformerConfigurationException e){
						e.printStackTrace();
					}catch(TransformerException e){
						e.printStackTrace();
					}
				}
				//
				if(defaultDocument == null) defaultDocument=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				Element return_node=defaultDocument.createElement("return");
				//header output
				SOAPHeader soap_header=response.getSOAPHeader();
				Element output_header = operation.getOutputHeaderMessage();
				if(soap_header!=null && output_header!=null){
					NodeList node_list=(NodeList)soap_header.getChildNodes();
					for(int i=0;i<node_list.getLength();++i){
						Node node=node_list.item(i);
						if(node.getNodeType()==Node.TEXT_NODE) {
							return_node.appendChild(defaultDocument.createTextNode(node.getNodeValue()));
						}else{
							if(node.getNodeType()==Node.ELEMENT_NODE){
								//Element copy=defaultDocument.createElement(node.getNodeName());
								Element copy=defaultDocument.createElement(node.getLocalName());
								return_node.appendChild(copy);
								soapElement2Dom((Element)node,copy);
							}
						}
					}
				}
				//body output
				SOAPBody soap_body=response.getSOAPBody();
				resultFault= soap_body.getFault();
				NodeList node_list=(NodeList)soap_body.getChildNodes();
				for(int i=0;i<node_list.getLength();++i){
					Node node=node_list.item(i);
					if(node.getNodeType()==Node.TEXT_NODE) {
						return_node.appendChild(defaultDocument.createTextNode(node.getNodeValue()));
					}else{
						if(node.getNodeType()==Node.ELEMENT_NODE){
							//Element copy=defaultDocument.createElement(node.getNodeName());
							Element copy=defaultDocument.createElement(node.getLocalName());
							return_node.appendChild(copy);
							soapElement2Dom((Element)node,copy);
						}
					}
				}
				return return_node;
			}
			return null;
	}
	public SOAPFault getResultFault(){
		return resultFault;
	}
	protected void dom2SoapElement(SOAPEnvelope envelope,SOAPElement soap_element,Element element,boolean is_rpc) throws SOAPException {
		XPath xp = XPathFactory.newInstance().newXPath();
		try{
			String text=null;
			text=xp.evaluate("text()", element);
			if(text!=null&&text.trim().length()>0) soap_element.addTextNode(text);
			if(is_rpc){
				String type=element.getAttribute("type");
				if(type!=null && type.trim().length()>0)
					soap_element.addAttribute(envelope.createName(XSI_NAMESPACE_PREFIX+":type"), XSD_NAMESPACE_PREFIX+":"+type);
			}
		}catch(XPathExpressionException e){
			e.printStackTrace();
		}
		NamedNodeMap attrs = element.getAttributes();
		for(int i=0;i<attrs.getLength();++i){
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr)attrs.item(i);
			Name attrName = envelope.createName(attr.getName(), attr.getPrefix(), attr.getNamespaceURI());
			soap_element.addAttribute(attrName, attr.getValue());
		}
		NodeList children=element.getChildNodes();
		for(int i=0;i<children.getLength();++i){
			if(children.item(i).getNodeType() != Node.ELEMENT_NODE)continue;
			Element child_element = (Element)children.item(i);
			SOAPElement soap_child_element;
			if(elementFormDefault){
				Name child_name = envelope.createName(child_element.getNodeName(), defaultNamespacePrefix, defaultNamespaceURI);
				soap_child_element= soap_element.addChildElement(child_name);
			}else soap_child_element = soap_element.addChildElement(child_element.getNodeName());
			dom2SoapElement(envelope, soap_child_element, child_element,is_rpc);
		}
	}
	protected void soapElement2Dom(Element soap_element,Element element) throws SOAPException {
		XPath xp = XPathFactory.newInstance().newXPath();
		try{
			String text=null;
			text=xp.evaluate("text()", soap_element);
			if(text!=null&&text.trim().length()>0) element.appendChild(element.getOwnerDocument().createTextNode(text));
		}catch(XPathExpressionException e){
			e.printStackTrace();
		}
		NamedNodeMap attrs = soap_element.getAttributes();
		for(int i=0;i<attrs.getLength();++i){
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr)attrs.item(i);
			if(!"xmlns".equalsIgnoreCase(attr.getName()))element.setAttribute(attr.getName(), attr.getValue());
		}
		NodeList children=soap_element.getChildNodes();
		for(int i=0;i<children.getLength();++i){
			if(children.item(i).getNodeType() != Node.ELEMENT_NODE)continue;
			Element soap_child_element = (Element)children.item(i);
			//Element child_element=element.getOwnerDocument().createElement(soap_child_element.getNodeName());
			Element child_element=element.getOwnerDocument().createElement(soap_child_element.getLocalName());
			element.appendChild(child_element);
			soapElement2Dom(soap_child_element,child_element);
		}
	}

}
