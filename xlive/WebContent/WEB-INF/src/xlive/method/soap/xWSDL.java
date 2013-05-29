package xlive.method.soap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class xWSDL {
	private List<xServiceInfo> serviceList=null;
	private Document wsdlDocument;
	private Element wsdlRoot;
	private String wsdlTargetNamespace="";
	private XPath xp;
	private List<Element> schemaList = null;
	private Map<String,String> namespaceMap;
	private String schemaElementFormDefault="";
    public static final String NS_URI_XSD_1999 = "http://www.w3.org/1999/XMLSchema";
    public static final String NS_URI_XSD_2000 = "http://www.w3.org/2000/10/XMLSchema";
    public static final String NS_URI_XSD_2001 = "http://www.w3.org/2001/XMLSchema";
	public final static String DEFAULT_SOAP_ENCODING_STYLE = "http://schemas.xmlsoap.org/soap/encoding/";

	public xWSDL(){}
	public List<xServiceInfo> build(String wsdl_url) throws SAXException,IOException,ParserConfigurationException,XPathExpressionException{
		serviceList = Collections.synchronizedList(new ArrayList<xServiceInfo>());
		schemaList = Collections.synchronizedList(new ArrayList<Element>());
		namespaceMap= new HashMap<String,String>();
		xp = XPathFactory.newInstance().newXPath();
		wsdlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(wsdl_url);
		wsdlRoot=wsdlDocument.getDocumentElement();
		buildSchema();
		buildServices();
        return serviceList;
	}
	protected void buildSchema()throws SAXException,IOException,ParserConfigurationException,XPathExpressionException{
		String xpath="/definitions/types/schema";
		NodeList node_list=(NodeList)xp.evaluate(xpath, wsdlRoot, XPathConstants.NODESET);
		for(int i=0; i<node_list.getLength();++i){
			Element schema=(Element)node_list.item(i);
			handleSchema(schema,null);
		}
	}
	protected void handleSchema(Element schema,Map<String,String> remember_uri)throws XPathExpressionException,SAXException,IOException,ParserConfigurationException{
		if(schema==null)return;
		if(remember_uri==null) remember_uri= new HashMap<String,String>();
		schemaList.add(schema);
		NamedNodeMap attrs=schema.getAttributes();
		for(int i=0; i<attrs.getLength();++i){
			Node node=attrs.item(i);
			String[] names=node.getNodeName().split(":");
			if(names.length>1 && "xmlns".equalsIgnoreCase(names[0])) namespaceMap.put(names[1], node.getNodeValue());
			if("elementFormDefault".equalsIgnoreCase(node.getNodeName())&& "qualified".equalsIgnoreCase(node.getNodeValue())){
				schemaElementFormDefault="qualified";
			}
		}
		String xpath="include";
		NodeList node_list=(NodeList)xp.evaluate(xpath, schema, XPathConstants.NODESET);
		for(int i=0; i<node_list.getLength();++i){
			Element include=(Element)node_list.item(i);
			String schema_location=include.getAttribute("schemaLocation");
			if(schema_location==null||schema_location.trim().length()==0) continue;
			if(remember_uri.containsKey(schema_location)) continue;
			remember_uri.put(schema_location, schema_location);
			Element include_schema=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(schema_location).getDocumentElement();
			handleSchema(include_schema, remember_uri);
		}
		xpath="import";
		node_list=(NodeList)xp.evaluate(xpath, schema, XPathConstants.NODESET);
		for(int i=0; i<node_list.getLength();++i){
			Element include=(Element)node_list.item(i);
			String schema_location=include.getAttribute("schemaLocation");
			if(schema_location==null||schema_location.trim().length()==0) continue;
			if(remember_uri.containsKey(schema_location)) continue;
			remember_uri.put(schema_location, schema_location);
			Element include_schema=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(schema_location).getDocumentElement();
			handleSchema(include_schema,remember_uri);
		}
	}
	protected void buildServices()throws XPathExpressionException{
		wsdlTargetNamespace=xp.evaluate("/definitions/@targetNamespace", wsdlRoot);
		Element definition=(Element)xp.evaluate("/definitions", wsdlRoot, XPathConstants.NODE);
		NamedNodeMap attrs=definition.getAttributes();
		for(int i=0; i<attrs.getLength();++i){
			Node node=attrs.item(i);
			String[] names=node.getNodeName().split(":");
			if(names.length>1 && "xmlns".equalsIgnoreCase(names[0])) namespaceMap.put(names[1], node.getNodeValue());
		}
		String xpath="/definitions/service";
		NodeList node_list=(NodeList)xp.evaluate(xpath, wsdlRoot, XPathConstants.NODESET);
		for(int i=0;i<node_list.getLength();++i){
           	xServiceInfo service_info = new xServiceInfo();
           	handleService(service_info, (Element)node_list.item(i));
           	serviceList.add(service_info);
		}
	}
	private String getLocalPart(String name){
		String[]names=name.split(":");
		return (names.length>1)?names[1]:name;
	}
	protected void handleService(xServiceInfo service_info, Element service)throws XPathExpressionException{
		String name=service.getAttribute("name");
		service_info.setName(name);
		String xpath="port";
		NodeList node_list=(NodeList)xp.evaluate(xpath, service, XPathConstants.NODESET);
		for(int i=0;i<node_list.getLength();++i){
			Element port=(Element)node_list.item(i);
			String location=xp.evaluate("address/@location", port);
			String attr_binding=port.getAttribute("binding");
			if(attr_binding != null){
				xpath="/definitions/binding[@name=\""+getLocalPart(attr_binding)+"\"]";
				Element binding = (Element)xp.evaluate(xpath, wsdlRoot, XPathConstants.NODE);
				List<xOperationInfo> operations = buildOperations(binding);
				Iterator<xOperationInfo> operations_iterator = operations.iterator();
				while(operations_iterator.hasNext()){
					xOperationInfo operation = (xOperationInfo)operations_iterator.next();
					operation.setSoapAddressLocation(location);
					operation.setServiceName(service_info.getName());
					service_info.addOperation(operation);
				}
			}
		}
	}
	private List<xOperationInfo> buildOperations(Element binding)throws XPathExpressionException{
		List<xOperationInfo> operation_infos = new ArrayList<xOperationInfo>();
		String xpath="binding/@style";
		String style=xp.evaluate(xpath, binding);
		if(style==null||style.trim().length()==0) style="document";
		xpath="binding/@transport";
		String transport=xp.evaluate(xpath, binding);
		xpath="binding/@verb";
		String verb=xp.evaluate(xpath, binding);
		xpath="/definitions/portType[@name=\""+getLocalPart(binding.getAttribute("type"))+"\"]";
		Element port_type=(Element)xp.evaluate(xpath,wsdlRoot,XPathConstants.NODE);
		xpath="operation";
		NodeList node_list=(NodeList)xp.evaluate(xpath, binding, XPathConstants.NODESET);
		for(int i=0;i<node_list.getLength();++i){
			Element binding_operation=(Element)node_list.item(i);
			xOperationInfo operation_info = new xOperationInfo();
			operation_info.setSoapBindingStyle(style);
			operation_info.setSoapBindingTransport(transport);
			operation_info.setSoapBindingVerb(verb);
			operation_info.setTargetNamespace(wsdlTargetNamespace);
			operation_info.setSchemaElementFormDefault(schemaElementFormDefault);
			buildOperation(operation_info, binding_operation,port_type);
			operation_infos.add(operation_info);
		}
		return operation_infos;
	}
	private xOperationInfo buildOperation(xOperationInfo operation_info, Element binding_operation, Element port_type)throws XPathExpressionException{
		String operation_name=binding_operation.getAttribute("name");
		operation_info.setOperationName(operation_name);
		String xpath="operation/@style";
		String operation_style=xp.evaluate(xpath, binding_operation);
		operation_info.setSoapOperationStyle(operation_style);
		xpath="operation/@soapAction";
		String soap_action=xp.evaluate(xpath, binding_operation);
		operation_info.setSoapOperationSoapAction(soap_action);
		//
		xpath="input";
		Element binding_input=(Element)xp.evaluate(xpath, binding_operation, XPathConstants.NODE);
		if(binding_input != null){
			xpath="body/@use";
			String soap_body_use=xp.evaluate(xpath, binding_input);
			operation_info.setInputSoapBodyUse(soap_body_use);
			xpath="body/@encodingStyle";
			String soap_body_encodingstyle=xp.evaluate(xpath, binding_input);
			if(soap_body_encodingstyle==null||soap_body_encodingstyle.trim().length()==0)
				soap_body_encodingstyle=DEFAULT_SOAP_ENCODING_STYLE;
			operation_info.setInputSoapBodyEncodingStyle(soap_body_encodingstyle);
			xpath="body/@namespace";
			String soap_body_namespace=xp.evaluate(xpath, binding_input);
			operation_info.setInputSoapBodyNamespace(soap_body_namespace);
				
			xpath="operation[@name=\""+getLocalPart(binding_operation.getAttribute("name"))+"\"]/input/@message";
			String input_message_name=(String)xp.evaluate(xpath, port_type);
			operation_info.setInputMessageName(getLocalPart(input_message_name));
			xpath="/definitions/message[@name=\""+getLocalPart(input_message_name)+"\"]";
			Element input_message=(Element)xp.evaluate(xpath, wsdlRoot, XPathConstants.NODE);
			operation_info.setInputMessage(buildMessageText(operation_info, input_message));
			// soap header
			xpath="header";
			Element header=(Element)xp.evaluate(xpath, binding_input, XPathConstants.NODE);
			if(header!=null){
				operation_info.setInputSoapHeaderUse(header.getAttribute("use"));
				operation_info.setInputHeaderMessageName(header.getAttribute("part"));
				xpath="/definitions/message[@name=\""+getLocalPart(header.getAttribute("message"))+"\"]";
				Element header_message=(Element)xp.evaluate(xpath, wsdlRoot, XPathConstants.NODE);
				operation_info.setInputHeaderMessage(buildMessageText(operation_info, header_message));
			}
		}
		//
		xpath="output";
		Element binding_output=(Element)xp.evaluate(xpath, binding_operation, XPathConstants.NODE);
		if(binding_output != null){
			xpath="body/@use";
			String soap_body_use=xp.evaluate(xpath, binding_output);
			operation_info.setOutputSoapBodyUse(soap_body_use);
			xpath="body/@encodingStyle";
			String soap_body_encodingstyle=xp.evaluate(xpath, binding_output);
			if(soap_body_encodingstyle==null||soap_body_encodingstyle.trim().length()==0)
				soap_body_encodingstyle=DEFAULT_SOAP_ENCODING_STYLE;
			operation_info.setInputSoapBodyEncodingStyle(soap_body_encodingstyle);
			xpath="body/@namespace";
			String soap_body_namespace=xp.evaluate(xpath, binding_output);
			operation_info.setInputSoapBodyNamespace(soap_body_namespace);
			
			xpath="operation[@name=\""+getLocalPart(binding_operation.getAttribute("name"))+"\"]/output/@message";
			String output_message_name=(String)xp.evaluate(xpath, port_type);
			operation_info.setOutputMessageName(getLocalPart(output_message_name));
			xpath="/definitions/message[@name=\""+getLocalPart(output_message_name)+"\"]";
			Element output_message=(Element)xp.evaluate(xpath, wsdlRoot, XPathConstants.NODE);
			operation_info.setOutputMessage(buildMessageText(operation_info, output_message));
			// soap header
			xpath="header";
			Element header=(Element)xp.evaluate(xpath, binding_output, XPathConstants.NODE);
			if(header!=null){
				operation_info.setOutputSoapHeaderUse(header.getAttribute("use"));
				operation_info.setOutputHeaderMessageName(header.getAttribute("part"));
				xpath="/definitions/message[@name=\""+getLocalPart(header.getAttribute("message"))+"\"]";
				Element header_message=(Element)xp.evaluate(xpath, wsdlRoot, XPathConstants.NODE);
				operation_info.setOutputHeaderMessage(buildMessageText(operation_info, header_message));
			}
		}
		//fault
		xpath="fault";
		Element binding_fault=(Element)xp.evaluate(xpath, binding_operation, XPathConstants.NODE);
		if(binding_fault != null){
			xpath="operation[@name=\""+getLocalPart(binding_operation.getAttribute("name"))+"\"]/false/@message";
			String fault_message_name=(String)xp.evaluate(xpath, port_type);
			operation_info.setFaultMessageName(getLocalPart(fault_message_name));
			xpath="/definitions/message[@name=\""+getLocalPart(fault_message_name)+"\"]";
			Element fault_message=(Element)xp.evaluate(xpath, wsdlRoot, XPathConstants.NODE);
			operation_info.setFaultMessage(buildMessageText(operation_info, fault_message));
		}
		//
		return operation_info;
	}
	private Element buildMessageText(xOperationInfo operation_inf,Element message)throws XPathExpressionException{
		Element root=message.getOwnerDocument().createElement(operation_inf.getOperationName());
		String xpath="part";
		NodeList node_list=(NodeList)xp.evaluate(xpath, message, XPathConstants.NODESET);
		for(int i=0;i<node_list.getLength();++i){
			Element part=(Element)node_list.item(i);
			String part_name=part.getAttribute("name");
			String part_type=part.getAttribute("type");
			String part_element=part.getAttribute("element");
			if(part_type!=null && part_type.trim().length()>0){
				String[] split=part_type.split(":");
				if(split.length>1 && this.isW3CNamespace(split[0])){
					Element part_name_element=message.getOwnerDocument().createElement(part_name);
					root.appendChild(part_name_element);
					if("rpc".equalsIgnoreCase(operation_inf.getSoapOperationStyle()))part_name_element.setAttribute("type", split[1]);
				}else{ //compleType
					String name=(part_element==null||part_element.trim().length()==0)?split[1]:getLocalPart(part_element);
					Element schema_element=(Element)evaluateSchema("complexType[@name=\""+getLocalPart(name)+"\"]",XPathConstants.NODE);
					if(schema_element!=null) buildSchemaElement(schema_element,root);
				}
			}else{
				// part element
				//Element partelement_element=message.getOwnerDocument().createElement(part_name);
				//root.appendChild(partelement_element);
				Element schema_element=(Element)evaluateSchema("element[@name=\""+getLocalPart(part_element)+"\"]",XPathConstants.NODE);
				if(schema_element!=null) {
					Element element=root.getOwnerDocument().createElement(schema_element.getAttribute("name"));
					root.appendChild(element);
					buildSchemaElement(schema_element,element);
				}
			}
		}
		return root;
	}
	private void buildSchemaElement(Element schema_element,Element parent_element){
		NodeList node_list=getElements(schema_element);
		if(node_list==null){
			String type=schema_element.getAttribute("type");
			if(type !=null &&type.trim().length()>0){
				String[]prefix_name=type.split(":");
				if(!isW3CNamespace(prefix_name[0])){
					String type_name=(prefix_name.length>1)?prefix_name[1]:type;
					node_list=(org.w3c.dom.NodeList)evaluateSchema("complexType[@name=\""+type_name+"\"]//element",XPathConstants.NODESET);
				}
			}
		}
		Node same_parent=null;
		for(int i=0;node_list!=null&&i<node_list.getLength();++i){
			Element child_element=(Element)node_list.item(i);
			if(same_parent==null)same_parent=child_element.getParentNode();
			if(child_element.getParentNode()!=same_parent)continue;
			if(isSimpleType(child_element))
				parent_element.appendChild(parent_element.getOwnerDocument().createElement(child_element.getAttribute("name")));
			else {
				Element element_element=parent_element.getOwnerDocument().createElement(child_element.getAttribute("name"));
				parent_element.appendChild(element_element);
				buildSchemaElement(child_element,element_element);
			}
		}
	}
	private NodeList getElements(Element element) {
		try{
			NodeList node_list=(NodeList)xp.evaluate("descendant::element", element, XPathConstants.NODESET);
			if(node_list!=null && node_list.getLength()==0)return null;
			return node_list;
		}catch(XPathExpressionException e){			
			e.printStackTrace();
		}
		return null;
	}
	private boolean isSimpleType(Element element){
		String type=element.getAttribute("type");
		if(type !=null &&type.trim().length()>0){
			String[]prefix_name=type.split(":");
			if(prefix_name.length > 1 && isW3CNamespace(prefix_name[0]))return true;
		}
		try{
			Node node=(Node)xp.evaluate("simpleType", element, XPathConstants.NODE);
			return node!=null;
		}catch(XPathExpressionException e){			
			e.printStackTrace();
		}
		return false;
	}
	private boolean isW3CNamespace(String ns){
		String namespace=namespaceMap.get(ns);
		return NS_URI_XSD_2001.equalsIgnoreCase(namespace)||NS_URI_XSD_2000.equalsIgnoreCase(namespace)||NS_URI_XSD_1999.equalsIgnoreCase(namespace);
	}
	/*
	private Element evaluateSchema(String xpath) {
		Object found=evaluateSchema(xpath,XPathConstants.NODE);
		return (found!=null)?(org.w3c.dom.Element)found:null;
	}
	*/
	private Object evaluateSchema(String xpath, javax.xml.namespace.QName return_type) {
		Object found;
		Iterator<Element> iterator=schemaList.iterator();
		while(iterator.hasNext()){
			Element schema=iterator.next();
			try{
				found=xp.evaluate(xpath, schema, return_type);
				if(found != null){
					if(return_type!=XPathConstants.NODESET) return found;
					if(((NodeList)found).getLength()>0) return found;
				}
			}catch(XPathExpressionException e){			
				e.printStackTrace();
			}
		}
		return null;
	}
	public String toString(){
		String string="";
		Iterator<xServiceInfo> iterator=serviceList.iterator();
		while(iterator.hasNext()){
			xServiceInfo service_info=(xServiceInfo)iterator.next();
			string += service_info.toString();
		}
		return string;
	}
}
