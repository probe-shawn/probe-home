package xlive.method.soap;

import java.io.FileOutputStream;
import java.util.List;
import javax.xml.xpath.XPathConstants;

import xlive.xWebInformation;
import xlive.method.*;

import org.w3c.dom.*;

import xlive.xml.*;

public class xGetPropertiesMethod extends xDefaultMethod{
	
	public Object process()throws xMethodException{
		boolean is_built="true".equalsIgnoreCase(getProperties("is-built"));
		String prop_wsdl_url=getProperties("wsdl-url");
		String method_wsdl_url=getArguments("wsdl-url");
		//
		Node clone_properties=null;
		if(!is_built){
			boolean permanent=(prop_wsdl_url!=null && prop_wsdl_url.trim().length()>0);
			String wsdl=permanent ? prop_wsdl_url:method_wsdl_url;
			if(wsdl!=null && wsdl.trim().length()>0) {
				clone_properties=buildFromWSDL(wsdl, permanent);
			}
		}
	    if(clone_properties==null)clone_properties= getObjectPropertiesNode().cloneNode(true);
		Node exclude_node=(Node)getArguments("exclude",XPathConstants.NODE);
		if(exclude_node != null)xXmlDocument.extendNodes(clone_properties, exclude_node, "remove");
		Node return_properties=setMethodReturnArguments("", "properties", "");
		return_properties.getParentNode().replaceChild(clone_properties, return_properties);
		return true;
	}
	private Node buildFromWSDL(String wsdl, boolean permanent)throws xMethodException {
		String default_invoker_class=getProperties("invoker-class");
		if(default_invoker_class==null||default_invoker_class.trim().length()==0) default_invoker_class="xlive.method.soap.xDefaultInvokerMethod";
		Element virtual_object_node=createElement("object");
		Element virtual_properties_node=(Element)virtual_object_node.appendChild(createElement("properties"));
		xWSDL xwdsl_builder= new xWSDL();
		List<xServiceInfo> services_list=null;
		List<xOperationInfo> operations_list=null;
		try{
			services_list=xwdsl_builder.build(wsdl);
			if(services_list.size()>0){
				xServiceInfo service=services_list.get(0);
				operations_list = service.getOperations();
				for(int i=0;i<operations_list.size();++i){
					xOperationInfo operation=(xOperationInfo)operations_list.get(i);
					Element operation_node=createElement(operation.getOperationName());
					operation_node.setAttribute("service-name", service.getName());
					operation_node.setAttribute("targetNamespace", operation.getTargetNamespace());
					operation_node.setAttribute("schemaElementFormDefault", operation.getSchemaElementFormDefault());
					operation_node.setAttribute("addressLocation", operation.getSoapAddressLocation());
					operation_node.setAttribute("bindingStyle", operation.getSoapBindingStyle());
					operation_node.setAttribute("bindingTransport", operation.getSoapBindingTransport());
					operation_node.setAttribute("bindingVerb", operation.getSoapBindingVerb());
					operation_node.setAttribute("operationStyle", operation.getSoapOperationStyle());
					operation_node.setAttribute("operationSoapAction", operation.getSoapOperationSoapAction());
					operation_node.appendChild(createElement("end-point",operation.getSoapAddressLocation()));
					//
					Element input_header_message=operation.getInputHeaderMessage();
					if(input_header_message!=null){
						Element input_header=(Element)operation_node.appendChild(createElement("input-header"));
						input_header.setAttribute("name", operation.getInputHeaderMessageName());
						input_header.setAttribute("soapUse", operation.getInputSoapHeaderUse());
						Node child=input_header_message.getFirstChild();
						while(child != null){
							input_header.appendChild(input_header.getOwnerDocument().adoptNode(child.cloneNode(true)));
							child=child.getNextSibling();
						}
					}
					//
					Element input=(Element)operation_node.appendChild(createElement("input"));
					input.setAttribute("name", operation.getInputMessageName());
					input.setAttribute("soapBodyUse", operation.getInputSoapBodyUse());
					input.setAttribute("soapBodyNamespace", operation.getInputSoapBodyNamespace());
					input.setAttribute("soapBodyEncodingStyle", operation.getInputSoapBodyEncodingStyle());
					Element input_message=operation.getInputMessage();
					if(input_message!=null){
						Node child=input_message.getFirstChild();
						while(child != null){
							input.appendChild(input.getOwnerDocument().adoptNode(child.cloneNode(true)));
							child=child.getNextSibling();
						}
					}
					//
					Element return_node=(Element)operation_node.appendChild(createElement("return"));
					Element output_heade_messager=operation.getOutputHeaderMessage();
					if(output_heade_messager!=null){
						Node child=output_heade_messager.getFirstChild();
						while(child != null){
							return_node.appendChild(return_node.getOwnerDocument().adoptNode(child.cloneNode(true)));
							child=child.getNextSibling();
						}
					}
					return_node.setAttribute("name", operation.getOutputMessageName());
					return_node.setAttribute("soapBodyUse", operation.getOutputSoapBodyUse());
					return_node.setAttribute("soapBodyNamespace", operation.getOutputSoapBodyNamespace());
					return_node.setAttribute("soapBodyEncodingStyle", operation.getOutputSoapBodyEncodingStyle());
					Element output_message=operation.getOutputMessage();
					if(output_message!=null){
						Node child=output_message.getFirstChild();
						while(child != null){
							return_node.appendChild(return_node.getOwnerDocument().adoptNode(child.cloneNode(true)));
							child=child.getNextSibling();
						}
					}
					Element fault_message=operation.getFaultMessage();
					if(fault_message != null){
						Node child=fault_message.getFirstChild();
						while(child != null){
							return_node.appendChild(return_node.getOwnerDocument().adoptNode(child.cloneNode(true)));
							child=child.getNextSibling();
						}
					}
					//
					virtual_properties_node.appendChild(operation_node);
					Element method=(Element)virtual_object_node.appendChild(createElement("method"));
					method.setAttribute("name", operation.getOperationName());
					method.setAttribute("useClass", default_invoker_class);
				}
			}
			//
			if(permanent){
				//this.setProperties("is-built", "true");
				virtual_properties_node.appendChild(createElement("is-built", "true"));
				Element object_node=getObjectNode();
				String resname=(String)evaluate("./parent::*[@resName]/@resName", object_node, XPathConstants.STRING);
				String filename = xWebInformation.getWebDirectory()+resname.substring(1);
				Document xml_doc= new xXmlDocument().createDocument(filename);
				Element doc_object=xml_doc.getDocumentElement();
				xXmlDocument.extendNodes(doc_object, virtual_object_node, "overwrite", true, 999, false, null, null);
				FileOutputStream new_file_stream= new FileOutputStream(filename);
				new xXmlDocument().Transform(doc_object, new_file_stream);
				new_file_stream.close();
				xXmlDocument.extendNodes(object_node, virtual_object_node, "overwrite", true, 999, false, null, null);
			}
			return virtual_properties_node;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
		//FileOutputStream status_file_stream= new FileOutputStream("c://test.xml");
		//new xXmlDocument().Transform(properties_node, status_file_stream);
		//status_file_stream.close();
	}
}
