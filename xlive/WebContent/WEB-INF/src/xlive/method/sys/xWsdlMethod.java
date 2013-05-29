package xlive.method.sys;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathConstants;
import xlive.method.*;
import org.w3c.dom.*;

import xlive.xml.*;

public class xWsdlMethod extends xDefaultMethod{
	
	private Document document=null;
	private Element createNSElement(String ns, String name){
		try {
			if(document==null)document= DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		//return document.createElement(name);
		return(ns==null)?document.createElement(name):document.createElementNS(ns,ns+":"+name);
	}
	public Object process()throws xMethodException {
		extendDefaultPropertiesToArguments();
		Node clone_properties= getObjectPropertiesNode().cloneNode(true);
		Node exclude_node=(Node)getArguments("exclude",XPathConstants.NODE);
		if(exclude_node != null)xXmlDocument.extendNodes(clone_properties, exclude_node, "remove");
		//
		HttpServletRequest request=this.getServiceContext().getHttpServletRequest();
		String loc=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getRequestURI();
		Element wsdl=this.createNSElement(null,"definitions");
		wsdl.setAttribute("targetNamespace", "http://probe.com.tw/webservices/");
		wsdl.setAttribute("xmlns:tns","http://probe.com.tw/webservices/");
		wsdl.setAttribute("xmlns:xsd1", "http://probe.com.tw/ws.xsd");
		wsdl.setAttribute("xmlns:soap", "http://schemas.xmlsoap.org/soap/");
		wsdl.setAttribute("xmlns","http://schemas.xmlsoap.org/wsdl/");
		/*
		//wsdl.setAttribute("xmlns:soapenc","http://schemas.xmlsoap.org/soap/encoding");
		//wsdl.setAttribute("xmlns:xsd", "http://www.w3.org/2000/10/XMLSchema");
		*/
		NodeList node_list=(NodeList)this.evaluate("./*", clone_properties, XPathConstants.NODESET);
		// make types
		Element types=this.createNSElement(null,"types");
		//types.setAttribute("name", this.getObjectPath("/")); 
		Element schema=(Element)types.appendChild(this.createNSElement(null,"schema"));
		schema.setAttribute("xmlns", "http://www.w3.org/2000/10/XMLSchema");
		schema.setAttribute("elementFormDefault", "qualified");
		schema.setAttribute("targetNamespace","http://probe.com.tw/ws.xsd");
		//messages
		Element messages=this.createNSElement(null,"messages");
		//port_types
		Element porttype=this.createNSElement(null,"portType");
		porttype.setAttribute("name", this.getObjectPath(".")+"PortType");
		//bindings
		Element binding=this.createNSElement(null,"binding");
		binding.setAttribute("name", this.getObjectPath(".")+"Binding");
		binding.setAttribute("type", "tns:"+this.getObjectPath(".")+"PortType");
		Element soap_binding=(Element)binding.appendChild(this.createNSElement("soap","binding"));
		soap_binding.setAttribute("style", "document");
		soap_binding.setAttribute("transport", "http://schemas.xmlsoap.org/soap/http");
		// service/port
		Element service=this.createNSElement(null,"service");
		service.setAttribute("name", this.getObjectPath("."));
		Element port=(Element)service.appendChild(this.createNSElement(null,"port"));
		port.setAttribute("name", this.getObjectPath(".")+"Soap");
		port.setAttribute("binding", "tns:"+this.getObjectPath(".")+"Binding");
		Element address=(Element)port.appendChild(this.createNSElement("soap","address"));
		address.setAttribute("location", loc);


		for(int i=0; i<node_list.getLength();++i){
			Element method=(Element)node_list.item(i);
			String node_name=method.getNodeName();
			if("resource-type".equalsIgnoreCase(node_name)) continue;
			if("description".equalsIgnoreCase(node_name)) continue;
			if("version".equalsIgnoreCase(node_name)) continue;
			//types/schema
			schema.appendChild(makeInputElementSchema(method,true));
			//
			Element type_output_element=(Element)schema.appendChild(this.createNSElement(null,"complexType"));
			type_output_element.setAttribute("name", node_name+"OutputType");
			Element output_sequence=(Element)type_output_element.appendChild(this.createNSElement(null, "sequence"));
			output_sequence.appendChild(makeOutputElementSchema(method));
			
			//schema.appendChild(makeOutputElementSchema(method));
			//
			Element message=(Element)messages.appendChild(this.createNSElement(null,"message"));
			message.setAttribute("name", node_name+"Input");
			Element part=(Element)message.appendChild(this.createNSElement(null,"part"));
			part.setAttribute("name", "body");
			part.setAttribute("element", "xsd1:"+node_name);
			//
			message=(Element)messages.appendChild(this.createNSElement(null,"message"));
			message.setAttribute("name", node_name+"Output");
			part=(Element)message.appendChild(this.createNSElement(null,"part"));
			part.setAttribute("name", "body");
			//part.setAttribute("element", "xsd1:"+node_name+"OutputType");
			part.setAttribute("type", "xsd1:"+node_name+"OutputType");
			//portType
			Element porttype_operation=(Element)porttype.appendChild(this.createNSElement(null,"operation"));
			porttype_operation.setAttribute("name", node_name);
			Element porttype_input=(Element)porttype_operation.appendChild(this.createNSElement(null,"input"));
			porttype_input.setAttribute("message", "tns:"+node_name+"Input");
			Element porttype_output=(Element)porttype_operation.appendChild(this.createNSElement(null,"output"));
			porttype_output.setAttribute("message", "tns:"+node_name+"Output");
			//bindings
			Element operation=(Element)binding.appendChild(this.createNSElement(null,"operation"));
			operation.setAttribute("name", node_name);
			Element soap_operation=(Element)operation.appendChild(this.createNSElement("soap","operation"));
			soap_operation.setAttribute("soapAction", loc);
			soap_operation.setAttribute("style", "document");
			Element input=(Element)operation.appendChild(this.createNSElement(null,"input"));
			Element input_soap_body=(Element)input.appendChild(this.createNSElement("soap","body"));
			input_soap_body.setAttribute("use", "literal");
			Element output=(Element)operation.appendChild(this.createNSElement(null,"output"));
			Element output_soap_body=(Element)output.appendChild(this.createNSElement("soap","body"));
			output_soap_body.setAttribute("use", "literal");
		}
		wsdl.appendChild(types);
		Node node;
		while((node=messages.getFirstChild())!=null)wsdl.appendChild(node);
		wsdl.appendChild(porttype);
		wsdl.appendChild(binding);
		wsdl.appendChild(service);
		
		HttpServletResponse response=this.getServiceContext().customizeResponse();
		java.io.OutputStream out=null;
		try{
			out=response.getOutputStream();
			response.setContentType("text/xml");
			response.setHeader("Cache-Control", "no-cache");
			new xXmlDocument().Transform(wsdl, out);
		}catch(TransformerConfigurationException tcx){
			tcx.printStackTrace();
			throw createMethodException("wsdl service", tcx.getLocalizedMessage());
	    }catch(TransformerException te){
	    	te.printStackTrace();
	    	throw createMethodException("wsdl service", te.getLocalizedMessage());
	    }catch(IOException ioe){
	    	throw createMethodException("wsdl service", ioe.getLocalizedMessage());
	    }finally{
	    	try{
	    		if(out != null){
	    			out.flush();
	    			out.close();
	    		}
	    	}catch(Exception e){}
	    }
		//
		//Node return_properties=setReturnArguments("properties", "", "");
		//return_properties.getParentNode().replaceChild(clone_properties, return_properties);
		return true;
	}
	private Element makeInputElementSchema(Element node, boolean first_level) throws xMethodException {
		Element element=this.createNSElement(null,"element");
		element.setAttribute("name", node.getNodeName());
		NodeList node_list=(NodeList)this.evaluate("./*", node, XPathConstants.NODESET);
		if(node_list.getLength()>0){
			Element complex_type=this.createNSElement(null,"complexType");
			Element sequence=(Element)complex_type.appendChild(this.createNSElement(null,"sequence"));
			boolean has_child=false;
			for(int i=0; i<node_list.getLength();++i){
				Element child_node=(Element)node_list.item(i);
				if(first_level && child_node.getNodeName()=="return") continue;
				sequence.appendChild(makeInputElementSchema(child_node,false));
				has_child=true;
			}
			if(has_child)element.appendChild(complex_type);
		}else element.setAttribute("type", "xsd:string");
		return element;
	}
	private Element makeOutputElementSchema(Element node) throws xMethodException {
		Element element=this.createNSElement(null,"element");
		element.setAttribute("name", node.getNodeName());
		element.setAttribute("type", "xsd:string");
		Node return_node=(Node)this.evaluate("./return", node, XPathConstants.NODE);
		if(return_node != null){
			element.appendChild(makeInputElementSchema((Element)return_node,false));
			/*
			NodeList node_list=(NodeList)this.evaluate("./*", return_node, XPathConstants.NODESET);
			if(node_list.getLength()>0){
				for(int i=0; i<node_list.getLength();++i){
					element.appendChild(makeInputElementSchema((Element)node_list.item(i),false));
				}
			}
			*/
		}
		return element;
	}

}
