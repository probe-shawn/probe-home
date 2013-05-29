package xlive.method.soap;

import java.net.MalformedURLException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import xlive.method.*;

import org.w3c.dom.*;


public class xDefaultInvokerMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		extendDefaultPropertiesToArguments();
		String why=null;
		xOperationInfo operationInfo=null;
		try{
			xSoapClient soap_client=new xSoapClient();
			//soap_client.setDumpRequestMessage(true);
			//soap_client.setDumpResponse(true);
			operationInfo=new xOperationInfo();
			operationInfo.setServiceName(getProperties("service-name"));
			String method_name=getMethodName();
			operationInfo.setOperationName(method_name);
			
			Element method_node=(Element)getQNameArguments(method_name, XPathConstants.NODE);
			
			operationInfo.setTargetNamespace(method_node.getAttribute("targetNamespace"));
			operationInfo.setSchemaElementFormDefault(method_node.getAttribute("schemaElementFormDefault"));
			operationInfo.setSoapOperationStyle(method_node.getAttribute("operationStyle"));
			operationInfo.setSoapOperationSoapAction(method_node.getAttribute("operationSoapAction"));
			operationInfo.setSoapBindingStyle(method_node.getAttribute("bindingStyle"));
			operationInfo.setSoapBindingTransport(method_node.getAttribute("bindingTransport"));
			operationInfo.setSoapBindingVerb(method_node.getAttribute("bindingVerb"));
			operationInfo.setSoapAddressLocation(getArguments("end-point"));
			//
			Element return_valid=setReturnArguments("valid", "false");
			soap_client.setDefaultDocument(return_valid.getOwnerDocument());
			//
			Element input_header=(Element)getArguments("input-header", XPathConstants.NODE);
			if(input_header!=null) operationInfo.setInputHeaderMessage(input_header);
			Element input=(Element)this.getArguments("input", XPathConstants.NODE);
			if(input != null) operationInfo.setInputMessage(input);
			//
			Node soap_return_node=soap_client.invokeOperation(operationInfo);
			//
			if(soap_return_node!=null){
				Node return_node=return_valid.getParentNode();
				Node child=soap_return_node.getFirstChild();
				while(child != null){
					return_node.appendChild(child);
					child=soap_return_node.getFirstChild();
				}
			}
			setReturnArguments("valid", "true");
			return getServiceContext().doNextProcess();
		}catch(ParserConfigurationException e){
			why="ParserConfigurationException :"+e.getLocalizedMessage();
			e.printStackTrace();
		}catch(UnsupportedOperationException e){
			why="UnsupportedOperationException :"+e.getLocalizedMessage();
			e.printStackTrace();
		}catch(MalformedURLException e){
			why="MalformedURLException :"+e.getLocalizedMessage();
			e.printStackTrace();
		}catch(javax.xml.soap.SOAPException e){
			why="SOAPException :"+e.getLocalizedMessage();
			e.printStackTrace();
		}
		setReturnArguments("valid", "false");
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess(false);
	}
}
